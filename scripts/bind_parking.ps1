$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Step 1: Fix SOLD spaces without owner -> FREE
$cmd = $conn.CreateCommand()
$cmd.CommandText = "UPDATE parking_space SET status='FREE' WHERE status='SOLD' AND (owner_id IS NULL OR owner_id=0)"
$n = $cmd.ExecuteNonQuery()
Write-Host "[Step 1] Reset $n SOLD-without-owner spaces to FREE"

# Step 2: Get owner -> building mapping (each owner gets 1 parking in their primary building)
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = @"
SELECT u.id as owner_id, cb.id as building_id, cb.building_no
FROM sys_user u
JOIN community_house h ON h.owner_id = u.id
JOIN community_unit cu ON cu.id = h.unit_id
JOIN community_building cb ON cb.id = cu.building_id
WHERE u.role='OWNER' AND u.status=1
ORDER BY u.id
"@
$r = $cmd2.ExecuteReader()
$ownerBuildings = @{}
while($r.Read()){
    $oid = [int]$r['owner_id']
    $bid = [int]$r['building_id']
    # Keep first building as primary (owner may have houses in multiple buildings)
    if(-not $ownerBuildings.ContainsKey($oid)){
        $ownerBuildings[$oid] = $bid
    }
}
$r.Close()

Write-Host "`n[Step 2] Owners to bind: $($ownerBuildings.Count)"

# Step 3: For each owner, find a FREE parking space in their building and bind
$bound = 0
$failed = 0
foreach($oid in ($ownerBuildings.Keys | Sort-Object)){
    $bid = $ownerBuildings[$oid]
    
    # Find a FREE space in the owner's building
    $cmd3 = $conn.CreateCommand()
    $cmd3.CommandText = "SELECT TOP 1 id, space_no FROM parking_space WHERE building_id=$bid AND status='FREE' ORDER BY row_no, col_no"
    $r3 = $cmd3.ExecuteReader()
    if($r3.Read()){
        $sid = [int]$r3['id']
        $sno = $r3['space_no']
        $r3.Close()
        
        # Update: set SOLD, owner_id, purchase_time
        $cmd4 = $conn.CreateCommand()
        $cmd4.CommandText = "UPDATE parking_space SET status='SOLD', owner_id=$oid, purchase_price=price, purchase_time=GETDATE(), update_time=GETDATE() WHERE id=$sid"
        $cmd4.ExecuteNonQuery() | Out-Null
        Write-Host "  Owner $oid -> Space $sno (id=$sid) bld=$bid"
        $bound++
    } else {
        $r3.Close()
        Write-Host "  Owner $oid -> NO FREE space in building $bid"
        $failed++
    }
}

Write-Host "`n[Step 3] Bound: $bound, Failed: $failed"

# Step 4: Verify final stats
$cmd5 = $conn.CreateCommand()
$cmd5.CommandText = "SELECT status, COUNT(*) as cnt FROM parking_space GROUP BY status ORDER BY status"
$r5 = $cmd5.ExecuteReader()
Write-Host "`n=== Final Status ==="
while($r5.Read()){ Write-Host "  $($r5['status']): $($r5['cnt'])" }
$r5.Close()

# Step 5: Show bound parking
$cmd6 = $conn.CreateCommand()
$cmd6.CommandText = "SELECT ps.space_no, ps.building_id, ps.owner_id, u.real_name FROM parking_space ps JOIN sys_user u ON ps.owner_id=u.id WHERE ps.status='SOLD' ORDER BY ps.building_id, ps.space_no"
$r6 = $cmd6.ExecuteReader()
Write-Host "`n=== SOLD with owner ==="
while($r6.Read()){ Write-Host "  $($r6['space_no']) bld=$($r6['building_id']) owner=$($r6['owner_id']) ($($r6['real_name']))" }
$r6.Close()

$conn.Close()
