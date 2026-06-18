$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function Exec($sql) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql; $c.ExecuteNonQuery() | Out-Null
}

# Step 1: Get mapping of old_id -> new_id (starting from 5)
$c = $conn.CreateCommand()
$c.CommandText = "SELECT id FROM sys_user WHERE id > 10000 ORDER BY id"
$r = $c.ExecuteReader()
$mapping = @{}
$newId = 5
while ($r.Read()) {
    $oldId = [int]$r["id"]
    $mapping[$oldId] = $newId
    Write-Host ("  Map: $oldId -> $newId")
    $newId++
}
$r.Close()
Write-Host "Mapping built: $($mapping.Count) entries"

# Step 2: Update foreign keys in related tables
foreach ($oldId in $mapping.Keys) {
    $nid = $mapping[$oldId]
    Exec "UPDATE access_card SET owner_id = $nid WHERE owner_id = $oldId"
    Exec "UPDATE feedback SET owner_id = $nid WHERE owner_id = $oldId"
    Exec "UPDATE feedback SET handler_id = $nid WHERE handler_id = $oldId"
    Exec "UPDATE feedback_reply SET user_id = $nid WHERE user_id = $oldId"
}
Write-Host "Foreign keys updated"

# Step 3: Reinsert sys_user rows with new IDs
# Enable IDENTITY_INSERT
Exec "SET IDENTITY_INSERT sys_user ON"

foreach ($oldId in ($mapping.Keys | Sort-Object)) {
    $nid = $mapping[$oldId]
    $sql = "INSERT INTO sys_user (id,username,password,real_name,phone,role,avatar_url,status,create_time,update_time) SELECT $nid,username,password,real_name,phone,role,avatar_url,status,create_time,update_time FROM sys_user WHERE id=$oldId"
    Exec $sql
    Write-Host "  Copied $oldId -> $nid"
}

# Disable IDENTITY_INSERT
Exec "SET IDENTITY_INSERT sys_user OFF"
Write-Host "New rows inserted with correct IDs"

# Step 4: Delete old rows (id > 10000)
Exec "DELETE FROM sys_user WHERE id > 10000"
Write-Host "Old high-ID rows deleted"

# Step 5: Reseed IDENTITY to next available value
Exec "DBCC CHECKIDENT ('sys_user', RESEED, $($newId - 1))"
Write-Host "IDENTITY reseeded to $($newId - 1)"

# Verify
$c2 = $conn.CreateCommand()
$c2.CommandText = "SELECT id, username, real_name, role FROM sys_user ORDER BY id"
$r2 = $c2.ExecuteReader()
Write-Host "`nFinal users:"
while ($r2.Read()) { Write-Host ("  id=" + $r2["id"] + " user=" + $r2["username"] + " role=" + $r2["role"]) }
$r2.Close()

# Verify FK tables
$c3 = $conn.CreateCommand()
$c3.CommandText = "SELECT 'access_card' AS tbl, MIN(owner_id) AS minid, MAX(owner_id) AS maxid FROM access_card UNION ALL SELECT 'feedback', MIN(owner_id), MAX(owner_id) FROM feedback UNION ALL SELECT 'feedback_reply', MIN(user_id), MAX(user_id) FROM feedback_reply"
$r3 = $c3.ExecuteReader()
Write-Host "`nFK check:"
while ($r3.Read()) { Write-Host ("  " + $r3["tbl"] + ": min=" + $r3["minid"] + " max=" + $r3["maxid"]) }
$r3.Close()

$conn.Close()
Write-Host "All done!"
