$conn = New-Object System.Data.SqlClient.SqlConnection("Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh")
$conn.Open()

$updates = @(
    @{id=1;  url='/uploads/facility/f01.svg'},
    @{id=2;  url='/uploads/facility/f02.svg'},
    @{id=3;  url='/uploads/facility/f03.svg'},
    @{id=4;  url='/uploads/facility/f04.svg'},
    @{id=5;  url='/uploads/facility/f05.svg'},
    @{id=6;  url='/uploads/facility/f06.svg'},
    @{id=7;  url='/uploads/facility/f07.svg'},
    @{id=8;  url='/uploads/facility/f08.svg'},
    @{id=9;  url='/uploads/facility/f09.svg'},
    @{id=10; url='/uploads/facility/f10.svg'},
    @{id=11; url='/uploads/facility/f11.svg'},
    @{id=12; url='/uploads/facility/f12.svg'},
    @{id=13; url='/uploads/facility/f13.svg'},
    @{id=14; url='/uploads/facility/f14.svg'},
    @{id=15; url='/uploads/facility/f15.svg'}
)

foreach ($u in $updates) {
    $cmd = $conn.CreateCommand()
    $cmd.CommandText = "UPDATE facility SET image_url = @url WHERE id = @id"
    $cmd.Parameters.AddWithValue("@url", $u.url) | Out-Null
    $cmd.Parameters.AddWithValue("@id", $u.id) | Out-Null
    $r = $cmd.ExecuteNonQuery()
    Write-Host "  id=$($u.id) -> $($u.url) (rows=$r)"
}

$conn.Close()
Write-Host "Done: updated 15 facility image_url records"
