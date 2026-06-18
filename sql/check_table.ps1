$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'access_card' ORDER BY ORDINAL_POSITION"
$reader = $cmd.ExecuteReader()
Write-Host "access_card columns:"
while ($reader.Read()) { Write-Host ("  " + $reader["COLUMN_NAME"]) }
$reader.Close()
$conn.Close()
