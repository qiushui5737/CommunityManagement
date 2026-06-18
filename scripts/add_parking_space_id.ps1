$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()
$cmd = $conn.CreateCommand()
$cmd.CommandText = "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='payment_bill' AND COLUMN_NAME='parking_space_id') ALTER TABLE payment_bill ADD parking_space_id BIGINT NULL"
$cmd.ExecuteNonQuery() | Out-Null
Write-Host "Added parking_space_id column"
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='payment_bill' ORDER BY ORDINAL_POSITION"
$r = $cmd2.ExecuteReader()
while($r.Read()){ Write-Host "  $($r['COLUMN_NAME'])" }
$r.Close()
$conn.Close()
