$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()
$cmd = $conn.CreateCommand()
$cmd.CommandText = "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='feedback' AND COLUMN_NAME='images') ALTER TABLE feedback ADD images NVARCHAR(2000) NULL"
$cmd.ExecuteNonQuery() | Out-Null
Write-Host "Added images column to feedback"
$conn.Close()
