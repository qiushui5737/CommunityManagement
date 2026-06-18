$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function Exec($sql) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql; $c.ExecuteNonQuery() | Out-Null
}

# Check current state
$c = $conn.CreateCommand()
$c.CommandText = "SELECT id, username FROM sys_user ORDER BY id"
$r = $c.ExecuteReader()
Write-Host "Current users:"
while ($r.Read()) { Write-Host ("  id=" + $r["id"] + " user=" + $r["username"]) }
$r.Close()

# Delete remaining old rows (id > 10000)
Exec "DELETE FROM sys_user WHERE id > 10000"
Write-Host "`nOld rows deleted"

# Check what IDs we have now
$c2 = $conn.CreateCommand()
$c2.CommandText = "SELECT id, username FROM sys_user ORDER BY id"
$r2 = $c2.ExecuteReader()
Write-Host "`nAfter cleanup:"
while ($r2.Read()) { Write-Host ("  id=" + $r2["id"] + " user=" + $r2["username"]) }
$r2.Close()

$conn.Close()
