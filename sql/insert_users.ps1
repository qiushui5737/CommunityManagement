$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function ParamInsert($sql, $params) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql
    foreach ($k in $params.Keys) {
        $p = New-Object System.Data.SqlClient.SqlParameter($k, $params[$k])
        $c.Parameters.Add($p) | Out-Null
    }
    $c.ExecuteNonQuery() | Out-Null
}

$pw = '$2a$10$64HNHx2qnHoAYsrIlep.A.WzWnpbQxgOeAEsghrlGPG4oo9p8KETK'
$sql = "INSERT INTO sys_user (username,password,real_name,phone,role,status,create_time,update_time) VALUES (@u,@p,@n,@ph,@r,1,GETDATE(),GETDATE())"

# ====== ADMIN users ======
ParamInsert $sql @{ "@u"="zhangwei"; "@p"=$pw; "@n"="张伟"; "@ph"="13800001001"; "@r"="ADMIN" }
ParamInsert $sql @{ "@u"="liufang"; "@p"=$pw; "@n"="刘芳"; "@ph"="13800001002"; "@r"="ADMIN" }
ParamInsert $sql @{ "@u"="chenjie"; "@p"=$pw; "@n"="陈杰"; "@ph"="13800001003"; "@r"="ADMIN" }
Write-Host "ADMIN: 3 users done"

# ====== OWNER users ======
ParamInsert $sql @{ "@u"="wangming"; "@p"=$pw; "@n"="王明"; "@ph"="13900002001"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="lihua"; "@p"=$pw; "@n"="李华"; "@ph"="13900002002"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="zhaoyang"; "@p"=$pw; "@n"="赵阳"; "@ph"="13900002003"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="sunli"; "@p"=$pw; "@n"="孙丽"; "@ph"="13900002004"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="zhouqiang"; "@p"=$pw; "@n"="周强"; "@ph"="13900002005"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="wumin"; "@p"=$pw; "@n"="吴敏"; "@ph"="13900002006"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="xuhong"; "@p"=$pw; "@n"="徐红"; "@ph"="13900002007"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="gaoyu"; "@p"=$pw; "@n"="高宇"; "@ph"="13900002008"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="majuan"; "@p"=$pw; "@n"="马娟"; "@ph"="13900002009"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="huanglei"; "@p"=$pw; "@n"="黄磊"; "@ph"="13900002010"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="linxue"; "@p"=$pw; "@n"="林雪"; "@ph"="13900002011"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="zhengtao"; "@p"=$pw; "@n"="郑涛"; "@ph"="13900002012"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="hemiao"; "@p"=$pw; "@n"="何苗"; "@ph"="13900002013"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="luocheng"; "@p"=$pw; "@n"="罗成"; "@ph"="13900002014"; "@r"="OWNER" }
ParamInsert $sql @{ "@u"="songna"; "@p"=$pw; "@n"="宋娜"; "@ph"="13900002015"; "@r"="OWNER" }
Write-Host "OWNER: 15 users done"

# Verify
$c = $conn.CreateCommand()
$c.CommandText = "SELECT role, COUNT(*) AS cnt FROM sys_user GROUP BY role ORDER BY role"
$r = $c.ExecuteReader()
Write-Host "`nFinal counts:"
while ($r.Read()) { Write-Host ("  " + $r["role"] + ": " + $r["cnt"]) }
$r.Close()
$conn.Close()
Write-Host "All done!"
