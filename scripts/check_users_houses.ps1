$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

Write-Host "=== OWNER users ==="
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT id, username, role FROM sys_user WHERE role = 'OWNER' ORDER BY id"
$r = $cmd.ExecuteReader()
while($r.Read()){ Write-Host "  ID=$($r['id']) user=$($r['username'])" }
$r.Close()

Write-Host "`n=== All users count ==="
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT role, COUNT(*) as cnt FROM sys_user GROUP BY role"
$r2 = $cmd2.ExecuteReader()
while($r2.Read()){ Write-Host "  role=$($r2['role']) count=$($r2['cnt'])" }
$r2.Close()

Write-Host "`n=== Houses with owner_id ==="
$cmd3 = $conn.CreateCommand()
$cmd3.CommandText = "SELECT COUNT(*) as cnt FROM community_house WHERE owner_id IS NOT NULL"
$r3 = $cmd3.ExecuteReader()
while($r3.Read()){ Write-Host "  bound=$($r3['cnt'])" }
$r3.Close()

Write-Host "`n=== Houses total ==="
$cmd4 = $conn.CreateCommand()
$cmd4.CommandText = "SELECT COUNT(*) as cnt FROM community_house"
$r4 = $cmd4.ExecuteReader()
while($r4.Read()){ Write-Host "  total=$($r4['cnt'])" }
$r4.Close()

Write-Host "`n=== Already bound houses ==="
$cmd5 = $conn.CreateCommand()
$cmd5.CommandText = "SELECT h.id, h.room_no, h.owner_id, u.unit_no, b.building_no FROM community_house h JOIN community_unit u ON h.unit_id = u.id JOIN community_building b ON u.building_id = b.id WHERE h.owner_id IS NOT NULL ORDER BY h.id"
$r5 = $cmd5.ExecuteReader()
while($r5.Read()){ Write-Host "  house=$($r5['id']) room=$($r5['building_no'])-$($r5['unit_no'])-$($r5['room_no']) owner=$($r5['owner_id'])" }
$r5.Close()

Write-Host "`n=== Sample unbound houses (first 15) ==="
$cmd6 = $conn.CreateCommand()
$cmd6.CommandText = "SELECT TOP 15 h.id, h.room_no, u.unit_no, b.building_no FROM community_house h JOIN community_unit u ON h.unit_id = u.id JOIN community_building b ON u.building_id = b.id WHERE h.owner_id IS NULL ORDER BY h.id"
$r6 = $cmd6.ExecuteReader()
while($r6.Read()){ Write-Host "  house=$($r6['id']) room=$($r6['building_no'])-$($r6['unit_no'])-$($r6['room_no'])" }
$r6.Close()

$conn.Close()
