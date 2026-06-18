$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()
$cmd = $conn.CreateCommand()
$cmd.CommandText = @"
IF OBJECT_ID('access_card', 'U') IS NOT NULL DROP TABLE access_card;
CREATE TABLE access_card (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    card_no NVARCHAR(50) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    card_type NVARCHAR(20) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    building_ids NVARCHAR(500),
    valid_from DATE,
    valid_to DATE,
    remark NVARCHAR(500),
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME DEFAULT GETDATE()
);
SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'access_card' ORDER BY ORDINAL_POSITION;
"@
$reader = $cmd.ExecuteReader()
Write-Host "New access_card columns:"
while ($reader.Read()) { Write-Host ("  " + $reader["COLUMN_NAME"]) }
$reader.Close()
$conn.Close()
Write-Host "Done!"
