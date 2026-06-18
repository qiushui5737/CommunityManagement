$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

$sqls = @(
    "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' AND COLUMN_NAME='email') ALTER TABLE sys_user ADD email NVARCHAR(100) NULL",
    "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' AND COLUMN_NAME='gender') ALTER TABLE sys_user ADD gender NVARCHAR(10) NULL",
    "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' AND COLUMN_NAME='birthday') ALTER TABLE sys_user ADD birthday DATE NULL",
    "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' AND COLUMN_NAME='id_card') ALTER TABLE sys_user ADD id_card NVARCHAR(20) NULL",
    "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' AND COLUMN_NAME='emergency_contact') ALTER TABLE sys_user ADD emergency_contact NVARCHAR(50) NULL",
    "IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' AND COLUMN_NAME='emergency_phone') ALTER TABLE sys_user ADD emergency_phone NVARCHAR(20) NULL"
)

foreach ($sql in $sqls) {
    $cmd = $conn.CreateCommand()
    $cmd.CommandText = $sql
    $cmd.ExecuteNonQuery() | Out-Null
}
Write-Host "All columns added successfully"

# Verify
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'sys_user' ORDER BY ORDINAL_POSITION"
$r = $cmd2.ExecuteReader()
Write-Host "Current columns:"
while ($r.Read()) { Write-Host "  $($r[0])" }
$r.Close()

$conn.Close()
