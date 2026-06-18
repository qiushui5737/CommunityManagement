$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

# Unbind house 557 from admin (user 1)
$cmd1 = $conn.CreateCommand()
$cmd1.CommandText = "UPDATE community_house SET owner_id = NULL WHERE id = 557"
$cmd1.ExecuteNonQuery() | Out-Null
Write-Host "Unbound house 557 from admin"

# Check if testuser (id=3) already has a house
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT id, room_no FROM community_house WHERE owner_id = 3"
$r = $cmd2.ExecuteReader()
$existing = @()
while ($r.Read()) { $existing += "  Already bound: house_id=$($r['id']), room=$($r['room_no'])" }
$r.Close()
if ($existing.Count -gt 0) { $existing | ForEach-Object { Write-Host $_ } }

# Bind house 557 to testuser (id=3)
$cmd3 = $conn.CreateCommand()
$cmd3.CommandText = "UPDATE community_house SET owner_id = 3 WHERE id = 557"
$rows = $cmd3.ExecuteNonQuery()
Write-Host "Bound house 557 to testuser (id=3): $rows row(s) updated"

# Verify
$cmd4 = $conn.CreateCommand()
$cmd4.CommandText = "SELECT h.id, h.room_no, u.unit_no, b.building_no, b.name as bname FROM community_house h JOIN community_unit u ON h.unit_id = u.id JOIN community_building b ON u.building_id = b.id WHERE h.owner_id = 3"
$r4 = $cmd4.ExecuteReader()
while ($r4.Read()) { Write-Host "Verified: testuser house = id=$($r4['id']), room=$($r4['room_no']), unit=$($r4['unit_no']), building=$($r4['building_no']) $($r4['bname'])" }
$r4.Close()

$conn.Close()
