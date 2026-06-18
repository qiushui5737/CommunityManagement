$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('feedback','feedback_reply','access_card') ORDER BY TABLE_NAME"
$reader = $cmd.ExecuteReader()
Write-Host "Tables:"
while ($reader.Read()) { Write-Host ("  " + $reader["TABLE_NAME"]) }
$reader.Close()
$conn.Close()
