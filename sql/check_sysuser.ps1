$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sys_user' ORDER BY ORDINAL_POSITION"
$r = $cmd.ExecuteReader()
while ($r.Read()) { Write-Host "$($r[0]) | $($r[1]) | $($r[2])" }
$r.Close()
$conn.Close()
