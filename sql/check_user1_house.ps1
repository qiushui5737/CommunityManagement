$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

# Check user 1
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT id, username, real_name, role FROM sys_user WHERE id = 1"
$r = $cmd.ExecuteReader()
if ($r.Read()) { Write-Host "User1: id=$($r['id']), username=$($r['username']), realName=$($r['real_name']), role=$($r['role'])" }
$r.Close()

# Check current house binding for user 1
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT id, unit_id, room_no, owner_id FROM community_house WHERE owner_id = 1"
$r2 = $cmd2.ExecuteReader()
$count = 0
while ($r2.Read()) { Write-Host "  Bound house: id=$($r2['id']), room=$($r2['room_no']), unit_id=$($r2['unit_id'])"; $count++ }
$r2.Close()
if ($count -eq 0) { Write-Host "  No house bound to user 1" }

# Check available unbound houses
$cmd3 = $conn.CreateCommand()
$cmd3.CommandText = "SELECT TOP 5 h.id, h.room_no, h.unit_id, u.unit_no, b.building_no, b.name as bname FROM community_house h JOIN community_unit u ON h.unit_id = u.id JOIN community_building b ON u.building_id = b.id WHERE h.owner_id IS NULL ORDER BY h.id"
$r3 = $cmd3.ExecuteReader()
Write-Host "`nAvailable unbound houses:"
while ($r3.Read()) { Write-Host "  house_id=$($r3['id']), room=$($r3['room_no']), unit=$($r3['unit_no']), building=$($r3['building_no']) $($r3['bname'])" }
$r3.Close()

$conn.Close()
