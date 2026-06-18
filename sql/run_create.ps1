$c = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$c.Open()
$sql = @"
CREATE TABLE access_card (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    card_no NVARCHAR(50) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    card_type NVARCHAR(20) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    building_ids NVARCHAR(500),
    valid_from DATE,
    valid_to DATE,
    remark NVARCHAR(200),
    create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
    update_time DATETIME2
)
"@
$cmd = New-Object System.Data.SqlClient.SqlCommand($sql, $c)
$cmd.ExecuteNonQuery()

$cmd2 = New-Object System.Data.SqlClient.SqlCommand("CREATE INDEX idx_card_owner ON access_card(owner_id)", $c)
$cmd2.ExecuteNonQuery()

$cmd3 = New-Object System.Data.SqlClient.SqlCommand("CREATE INDEX idx_card_status ON access_card(status)", $c)
$cmd3.ExecuteNonQuery()

$cmd4 = New-Object System.Data.SqlClient.SqlCommand("CREATE INDEX idx_card_type ON access_card(card_type)", $c)
$cmd4.ExecuteNonQuery()

$c.Close()
Write-Host "access_card table created successfully"
