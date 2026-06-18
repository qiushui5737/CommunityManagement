$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

$tables = @('payment_bill', 'repair_request', 'parking_space', 'community_house', 'community_unit')
foreach ($t in $tables) {
    Write-Host "=== $t ==="
    $cmd = $conn.CreateCommand()
    $cmd.CommandText = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '$t' ORDER BY ORDINAL_POSITION"
    $reader = $cmd.ExecuteReader()
    while ($reader.Read()) { Write-Host "  $($reader[0])" }
    $reader.Close()
}

$conn.Close()
