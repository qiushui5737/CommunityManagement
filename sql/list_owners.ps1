$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT id, username, real_name, role FROM sys_user WHERE role = 'OWNER' ORDER BY id"
$r = $cmd.ExecuteReader()
Write-Host "OWNER users:"
while ($r.Read()) { Write-Host "  id=$($r['id']), username=$($r['username']), realName=$($r['real_name'])" }
$r.Close()

$conn.Close()
