$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Fix 1: All non-bound houses should be VACANT
$cmd1 = $conn.CreateCommand()
$cmd1.CommandText = "UPDATE community_house SET status = 'VACANT' WHERE owner_id IS NULL AND status <> 'VACANT'"
$n1 = $cmd1.ExecuteNonQuery()
Write-Host "Set $n1 non-bound houses to VACANT"

# Fix 2: All RENTED houses -> VACANT (should be none after fix 1, but just in case)
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "UPDATE community_house SET status = 'VACANT' WHERE status = 'RENTED'"
$n2 = $cmd2.ExecuteNonQuery()
Write-Host "Set $n2 RENTED houses to VACANT"

# Fix 3: All bound houses should be OCCUPIED
$cmd3 = $conn.CreateCommand()
$cmd3.CommandText = "UPDATE community_house SET status = 'OCCUPIED' WHERE owner_id IS NOT NULL AND status <> 'OCCUPIED'"
$n3 = $cmd3.ExecuteNonQuery()
Write-Host "Set $n3 bound houses to OCCUPIED"

# Verify
$cmd4 = $conn.CreateCommand()
$cmd4.CommandText = "SELECT status, COUNT(*) as cnt FROM community_house GROUP BY status ORDER BY status"
$r = $cmd4.ExecuteReader()
Write-Host "`nStatus distribution:"
while($r.Read()){ Write-Host "  $($r['status']): $($r['cnt'])" }
$r.Close()

# Verify bound vs status consistency
$cmd5 = $conn.CreateCommand()
$cmd5.CommandText = "SELECT COUNT(*) as cnt FROM community_house WHERE owner_id IS NOT NULL AND status <> 'OCCUPIED'"
$r5 = $cmd5.ExecuteReader()
if ($r5.Read() -and $r5['cnt'] -gt 0) { Write-Host "`nWARNING: $($r5['cnt']) bound houses not OCCUPIED!" } else { Write-Host "`nBound houses all OCCUPIED - OK" }
$r5.Close()

$cmd6 = $conn.CreateCommand()
$cmd6.CommandText = "SELECT COUNT(*) as cnt FROM community_house WHERE owner_id IS NULL AND status <> 'VACANT'"
$r6 = $cmd6.ExecuteReader()
if ($r6.Read() -and $r6['cnt'] -gt 0) { Write-Host "WARNING: $($r6['cnt']) unbound houses not VACANT!" } else { Write-Host "Unbound houses all VACANT - OK" }
$r6.Close()

$conn.Close()
Write-Host "`nDone!"
