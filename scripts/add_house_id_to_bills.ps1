$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Add house_id column
$cmd = $conn.CreateCommand()
$cmd.CommandText = "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='payment_bill' AND COLUMN_NAME='house_id') ALTER TABLE payment_bill ADD house_id BIGINT NULL"
$cmd.ExecuteNonQuery() | Out-Null
Write-Host "Added house_id column"

# Verify
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='payment_bill' ORDER BY ORDINAL_POSITION"
$r = $cmd2.ExecuteReader()
Write-Host "`npayment_bill columns:"
while($r.Read()){ Write-Host "  $($r['COLUMN_NAME']) ($($r['DATA_TYPE']))" }
$r.Close()

$conn.Close()
