$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()
$c = $conn.CreateCommand()
$c.CommandText = "SELECT id, username, real_name, role FROM sys_user ORDER BY id"
$r = $c.ExecuteReader()
Write-Host "Current users:"
while ($r.Read()) { Write-Host ("  id=" + $r["id"] + " user=" + $r["username"] + " name=" + $r["real_name"] + " role=" + $r["role"]) }
$r.Close()
$conn.Close()
