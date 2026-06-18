$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# 1. Total parking spaces by status
$cmd = $conn.CreateCommand()
$cmd.CommandText = "SELECT status, COUNT(*) as cnt FROM parking_space GROUP BY status"
$r = $cmd.ExecuteReader()
Write-Host "=== Parking Space Status ==="
while($r.Read()){ Write-Host "  $($r['status']): $($r['cnt'])" }
$r.Close()

# 2. Count of sold spaces with owner
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT COUNT(*) as cnt FROM parking_space WHERE status='SOLD' AND owner_id IS NOT NULL"
$r2 = $cmd2.ExecuteReader()
if($r2.Read()){ Write-Host "`nSold with owner: $($r2['cnt'])" }
$r2.Close()

# 3. Count of FREE spaces
$cmd3 = $conn.CreateCommand()
$cmd3.CommandText = "SELECT COUNT(*) as cnt FROM parking_space WHERE status='FREE'"
$r3 = $cmd3.ExecuteReader()
if($r3.Read()){ Write-Host "FREE spaces: $($r3['cnt'])" }
$r3.Close()

# 4. Owners (role=OWNER, status=1)
$cmd4 = $conn.CreateCommand()
$cmd4.CommandText = "SELECT id, real_name FROM sys_user WHERE role='OWNER' AND status=1 ORDER BY id"
$r4 = $cmd4.ExecuteReader()
Write-Host "`n=== Owners ==="
$owners = @()
while($r4.Read()){ 
    Write-Host "  ID=$($r4['id']) Name=$($r4['real_name'])"
    $owners += $r4['id']
}
$r4.Close()

# 5. Parking spaces per building
$cmd5 = $conn.CreateCommand()
$cmd5.CommandText = "SELECT b.building_no, b.name, COUNT(ps.id) as cnt FROM parking_space ps JOIN community_building b ON ps.building_id=b.id GROUP BY b.building_no, b.name ORDER BY b.building_no"
$r5 = $cmd5.ExecuteReader()
Write-Host "`n=== Parking per Building ==="
while($r5.Read()){ Write-Host "  $($r5['building_no']) $($r5['name']): $($r5['cnt']) spaces" }
$r5.Close()

# 6. Sample FREE spaces
$cmd6 = $conn.CreateCommand()
$cmd6.CommandText = "SELECT TOP 5 id, space_no, building_id, zone, row_no, col_no, status FROM parking_space WHERE status='FREE' ORDER BY building_id, row_no, col_no"
$r6 = $cmd6.ExecuteReader()
Write-Host "`n=== Sample FREE spaces ==="
while($r6.Read()){ Write-Host "  id=$($r6['id']) no=$($r6['space_no']) bld=$($r6['building_id']) zone=$($r6['zone']) r=$($r6['row_no']) c=$($r6['col_no'])" }
$r6.Close()

$conn.Close()
