$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Check SOLD spaces without owner
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT COUNT(*) as cnt FROM parking_space WHERE status='SOLD' AND (owner_id IS NULL OR owner_id=0)"
$r = $cmd.ExecuteReader()
if($r.Read()){ Write-Host "SOLD without owner: $($r['cnt'])" }
$r.Close()

# Check SOLD spaces with valid owner
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT TOP 5 ps.id, ps.space_no, ps.building_id, ps.status, ps.owner_id FROM parking_space ps WHERE ps.status='SOLD' AND ps.owner_id IS NOT NULL AND ps.owner_id > 0"
$r2 = $cmd2.ExecuteReader()
Write-Host "`nSOLD with owner:"
while($r2.Read()){ Write-Host "  id=$($r2['id']) no=$($r2['space_no']) bld=$($r2['building_id']) owner=$($r2['owner_id'])" }
$r2.Close()

# Check owner-house-building mapping
$cmd3 = $conn.CreateCommand()
$cmd3.CommandText = @"
SELECT u.id as owner_id, u.real_name, h.id as house_id, h.room_no, cb.id as building_id, cb.building_no
FROM sys_user u
JOIN community_house h ON h.owner_id = u.id
JOIN community_unit cu ON cu.id = h.unit_id
JOIN community_building cb ON cb.id = cu.building_id
WHERE u.role='OWNER' AND u.status=1
ORDER BY u.id, cb.building_no
"@
$r3 = $cmd3.ExecuteReader()
Write-Host "`nOwner -> Building mapping:"
while($r3.Read()){ Write-Host "  owner=$($r3['owner_id']) ($($r3['real_name'])) -> bld=$($r3['building_id']) ($($r3['building_no'])) house=$($r3['room_no'])" }
$r3.Close()

# Parking per building IDs
$cmd4 = $conn.CreateCommand()
$cmd4.CommandText = "SELECT DISTINCT building_id FROM parking_space ORDER BY building_id"
$r4 = $cmd4.ExecuteReader()
Write-Host "`nParking building IDs:"
while($r4.Read()){ Write-Host "  $($r4['building_id'])" }
$r4.Close()

$conn.Close()
