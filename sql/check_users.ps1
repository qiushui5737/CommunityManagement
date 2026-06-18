$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function Exec($sql) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql; $c.ExecuteNonQuery() | Out-Null
}
function ParamInsert($sql, $params) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql
    foreach ($k in $params.Keys) {
        $p = New-Object System.Data.SqlClient.SqlParameter($k, $params[$k])
        $c.Parameters.Add($p) | Out-Null
    }
    $c.ExecuteNonQuery() | Out-Null
}

# Get existing password hash from user id=3
$c = $conn.CreateCommand()
$c.CommandText = "SELECT password FROM sys_user WHERE id=3"
$pwHash = $c.ExecuteScalar()
Write-Host "Existing pw hash: $pwHash"

# Check existing columns
$c2 = $conn.CreateCommand()
$c2.CommandText = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='sys_user' ORDER BY ORDINAL_POSITION"
$r = $c2.ExecuteReader()
Write-Host "sys_user columns:"
while ($r.Read()) { Write-Host ("  " + $r["COLUMN_NAME"]) }
$r.Close()

$conn.Close()
