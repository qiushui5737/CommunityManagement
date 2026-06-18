$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function Exec($sql) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql; $c.ExecuteNonQuery() | Out-Null
}

function ParamInsert($sql, $params) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql
    foreach ($k in $params.Keys) {
        $p = New-Object System.Data.SqlClient.SqlParameter($k, $params[$k])
        $c.Parameters.Add($p) | Out-Null
    }
    $c.ExecuteNonQuery() | Out-Null
}

$pw = '$2a$10$64HNHx2qnHoAYsrIlep.A.WzWnpbQxgOeAEsghrlGPG4oo9p8KETK'

# Enable IDENTITY_INSERT
Exec "SET IDENTITY_INSERT sys_user ON"

# ADMIN users (id 5-7)
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (5,@u,@p,@n,@ph,'ADMIN',1,GETDATE(),GETDATE())" @{ "@u"="zhangwei"; "@p"=$pw; "@n"="张伟"; "@ph"="13800001001" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (6,@u,@p,@n,@ph,'ADMIN',1,GETDATE(),GETDATE())" @{ "@u"="liufang"; "@p"=$pw; "@n"="刘芳"; "@ph"="13800001002" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (7,@u,@p,@n,@ph,'ADMIN',1,GETDATE(),GETDATE())" @{ "@u"="chenjie"; "@p"=$pw; "@n"="陈杰"; "@ph"="13800001003" }
Write-Host "ADMIN: 3 users inserted (id 5-7)"

# OWNER users (id 8-22)
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (8,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="wangming"; "@p"=$pw; "@n"="王明"; "@ph"="13900002001" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (9,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="lihua"; "@p"=$pw; "@n"="李华"; "@ph"="13900002002" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (10,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="zhaoyang"; "@p"=$pw; "@n"="赵阳"; "@ph"="13900002003" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (11,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="sunli"; "@p"=$pw; "@n"="孙丽"; "@ph"="13900002004" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (12,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="zhouqiang"; "@p"=$pw; "@n"="周强"; "@ph"="13900002005" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (13,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="wumin"; "@p"=$pw; "@n"="吴敏"; "@ph"="13900002006" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (14,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="xuhong"; "@p"=$pw; "@n"="徐红"; "@ph"="13900002007" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (15,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="gaoyu"; "@p"=$pw; "@n"="高宇"; "@ph"="13900002008" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (16,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="majuan"; "@p"=$pw; "@n"="马娟"; "@ph"="13900002009" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (17,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="huanglei"; "@p"=$pw; "@n"="黄磊"; "@ph"="13900002010" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (18,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="linxue"; "@p"=$pw; "@n"="林雪"; "@ph"="13900002011" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (19,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="zhengtao"; "@p"=$pw; "@n"="郑涛"; "@ph"="13900002012" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (20,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="hemiao"; "@p"=$pw; "@n"="何苗"; "@ph"="13900002013" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (21,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="luocheng"; "@p"=$pw; "@n"="罗成"; "@ph"="13900002014" }
ParamInsert "INSERT INTO sys_user (id,username,password,real_name,phone,role,status,create_time,update_time) VALUES (22,@u,@p,@n,@ph,'OWNER',1,GETDATE(),GETDATE())" @{ "@u"="songna"; "@p"=$pw; "@n"="宋娜"; "@ph"="13900002015" }
Write-Host "OWNER: 15 users inserted (id 8-22)"

# Disable IDENTITY_INSERT
Exec "SET IDENTITY_INSERT sys_user OFF"

# Reseed
Exec "DBCC CHECKIDENT ('sys_user', RESEED, 22)"
Write-Host "IDENTITY reseeded to 22"

# Verify
$c = $conn.CreateCommand()
$c.CommandText = "SELECT id, username, role FROM sys_user ORDER BY id"
$r = $c.ExecuteReader()
Write-Host "`nFinal users:"
while ($r.Read()) { Write-Host ("  id=" + $r["id"] + " user=" + $r["username"] + " role=" + $r["role"]) }
$r.Close()
$conn.Close()
Write-Host "`nAll done!"
