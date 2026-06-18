$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()
$cmd = $conn.CreateCommand()

# Check existing users
$cmd.CommandText = "SELECT id, username, real_name, role FROM sys_user"
$reader = $cmd.ExecuteReader()
Write-Host "=== sys_user ==="
while ($reader.Read()) { Write-Host ("  id=" + $reader["id"] + " username=" + $reader["username"] + " real_name=" + $reader["real_name"] + " role=" + $reader["role"]) }
$reader.Close()

# Check buildings
$cmd.CommandText = "SELECT id, name FROM community_building"
$reader = $cmd.ExecuteReader()
Write-Host "`n=== community_building ==="
while ($reader.Read()) { Write-Host ("  id=" + $reader["id"] + " name=" + $reader["name"]) }
$reader.Close()

$conn.Close()
