$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=True")
$conn.Open()

# Backfill house_id for existing bills (set to first house of the owner)
$cmd = $conn.CreateCommand()
$cmd.CommandText = @"
UPDATE pb
SET pb.house_id = (
    SELECT TOP 1 h.id FROM community_house h WHERE h.owner_id = pb.owner_id
)
FROM payment_bill pb
WHERE pb.house_id IS NULL AND pb.owner_id IS NOT NULL
"@
$n = $cmd.ExecuteNonQuery()
Write-Host "Backfilled $n bills with house_id"

# Verify
$cmd2 = $conn.CreateCommand()
$cmd2.CommandText = "SELECT COUNT(*) as total, SUM(CASE WHEN house_id IS NOT NULL THEN 1 ELSE 0 END) as with_house FROM payment_bill"
$r = $cmd2.ExecuteReader()
if ($r.Read()) { Write-Host "Total=$($r['total']) with_house=$($r['with_house'])" }
$r.Close()

$conn.Close()
