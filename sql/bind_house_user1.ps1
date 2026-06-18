$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

$cmd = $conn.CreateCommand()
$cmd.CommandText = "UPDATE community_house SET owner_id = 1 WHERE id = 557"
$rows = $cmd.ExecuteNonQuery()
Write-Host "Updated $rows row(s)"

# Verify
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT h.id, h.room_no, u.unit_no, b.building_no + ' ' + b.name as bld FROM community_house h JOIN community_unit u ON h.unit_id = u.id JOIN community_building b ON u.building_id = b.id WHERE h.owner_id = 1"
$r = $cmd2.ExecuteReader()
while ($r.Read()) { Write-Host "User1 house: id=$($r['id']), room=$($r['room_no']), unit=$($r['unit_no']), building=$($r['bld'])" }
$r.Close()

$conn.Close()
