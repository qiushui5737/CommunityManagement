$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Clear all
$cmd1 = $conn.CreateCommand()
$cmd1.CommandText = "UPDATE community_house SET owner_id = NULL, status = 'VACANT' WHERE owner_id IS NOT NULL"
$cmd1.ExecuteNonQuery() | Out-Null
Write-Host "Cleared all bindings"

# Owner IDs
$owners = @(3, 4, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)

# Get buildings count
$cmdB = $conn.CreateCommand()
$cmdB.CommandText = "SELECT id, building_no FROM community_building ORDER BY id"
$rB = $cmdB.ExecuteReader()
$buildings = @()
while($rB.Read()){ $buildings += @{ id=$rB['id']; no=$rB['building_no'] } }
$rB.Close()
Write-Host "Buildings: $($buildings.Count)"

# For each owner, pick a house from a different building (round-robin)
$cmd4 = $conn.CreateCommand()
for ($i = 0; $i -lt $owners.Count; $i++) {
    $oid = $owners[$i]
    $bIdx = $i % $buildings.Count
    $bid = $buildings[$bIdx].id

    # Pick first available vacant house in this building
    $cmd4.CommandText = @"
SELECT TOP 1 h.id
FROM community_house h
JOIN community_unit u ON h.unit_id = u.id
WHERE u.building_id = $bid AND h.owner_id IS NULL AND h.status = 'VACANT'
ORDER BY u.unit_no, h.room_no
"@
    $r = $cmd4.ExecuteReader()
    if ($r.Read()) {
        $hid = $r['id']
        $r.Close()
        $cmdU = $conn.CreateCommand()
        $cmdU.CommandText = "UPDATE community_house SET owner_id = $oid, status = 'OCCUPIED' WHERE id = $hid"
        $cmdU.ExecuteNonQuery() | Out-Null
        Write-Host "  owner $oid -> building $($buildings[$bIdx].no) house $hid"
    } else {
        $r.Close()
        Write-Host "  owner $oid -> NO VACANT house in building $($buildings[$bIdx].no)"
    }
}

# Verify
$cmd5 = $conn.CreateCommand()
$cmd5.CommandText = "SELECT su.username, su.id as uid, b.building_no, u.unit_no, h.room_no FROM community_house h JOIN community_unit u ON h.unit_id=u.id JOIN community_building b ON u.building_id=b.id JOIN sys_user su ON h.owner_id=su.id WHERE h.owner_id IS NOT NULL ORDER BY su.id"
$r5 = $cmd5.ExecuteReader()
Write-Host "`n=== Final bindings ==="
while($r5.Read()){
    Write-Host "  $($r5['username']) (ID=$($r5['uid'])) -> $($r5['building_no'])-$($r5['unit_no'])-$($r5['room_no'])"
}
$r5.Close()

$conn.Close()
Write-Host "Done!"
