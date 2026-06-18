$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Check all users with OWNER role
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT id, username, role FROM sys_user WHERE role = 'OWNER' ORDER BY id"
$r = $cmd.ExecuteReader()
Write-Host "=== OWNER users ==="
while($r.Read()){ Write-Host "  ID=$($r['id']) user=$($r['username'])" }
$r.Close()

# Check houses with owner_id set
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT h.id, h.room_no, h.owner_id, h.status, su.username FROM community_house h LEFT JOIN sys_user su ON h.owner_id = su.id WHERE h.owner_id IS NOT NULL ORDER BY h.owner_id, h.id"
$r2 = $cmd2.ExecuteReader()
Write-Host "`n=== Bound houses ==="
while($r2.Read()){ Write-Host "  house=$($r2['id']) room=$($r2['room_no']) status=$($r2['status']) owner=$($r2['owner_id']) ($($r2['username']))" }
$r2.Close()

$conn.Close()
