$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function Exec($sql) { $c = $conn.CreateCommand(); $c.CommandText = $sql; $c.ExecuteNonQuery() | Out-Null }
function PI($sql, $params) {
    $c = $conn.CreateCommand(); $c.CommandText = $sql
    foreach ($k in $params.Keys) {
        $v = $params[$k]
        if ($v -eq $null) { $v = [DBNull]::Value }
        $c.Parameters.Add((New-Object System.Data.SqlClient.SqlParameter($k, $v))) | Out-Null
    }
    $c.ExecuteNonQuery() | Out-Null
}
function RandDT($daysAgo) { (Get-Date).AddDays(-$daysAgo).AddHours((Get-Random -Min 6 -Max 22)).AddMinutes((Get-Random -Min 0 -Max 59)).ToString("yyyy-MM-dd HH:mm:ss") }

# === 1. Clean old access_card data and re-insert ===
Exec "DELETE FROM access_log"
Exec "DELETE FROM access_card"
Write-Host "Cleaned old data"

$cardSql = "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES (@cn,@oid,@ct,@st,@bi,@vf,@vt,@rm)"

PI $cardSql @{ "@cn"="AC20260105001";"@oid"=5; "@ct"="OWNER";"@st"="ACTIVE";"@bi"="1,2,3";"@vf"="2026-01-01";"@vt"="2027-12-31";"@rm"="管理员主卡" }
PI $cardSql @{ "@cn"="AC20260106001";"@oid"=6; "@ct"="OWNER";"@st"="ACTIVE";"@bi"="1,2,3,4,5,6";"@vf"="2026-01-01";"@vt"=$null;"@rm"="管理员全通卡" }
PI $cardSql @{ "@cn"="AC20260107001";"@oid"=7; "@ct"="OWNER";"@st"="ACTIVE";"@bi"="4,5,6";"@vf"="2026-03-01";"@vt"="2027-12-31";"@rm"="管理员主卡" }
PI $cardSql @{ "@cn"="AC20260208001";"@oid"=8; "@ct"="OWNER";"@st"="ACTIVE";"@bi"="1";"@vf"="2026-01-15";"@vt"="2028-01-14";"@rm"="1栋业主" }
PI $cardSql @{ "@cn"="AC20260208002";"@oid"=8; "@ct"="FAMILY";"@st"="ACTIVE";"@bi"="1";"@vf"="2026-01-15";"@vt"="2027-07-14";"@rm"="家属卡" }
PI $cardSql @{ "@cn"="AC20260209001";"@oid"=9; "@ct"="OWNER";"@st"="ACTIVE";"@bi"="1";"@vf"="2026-02-01";"@vt"="2028-01-31";"@rm"="1栋业主" }
PI $cardSql @{ "@cn"="AC20260210001";"@oid"=10;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="2";"@vf"="2026-02-15";"@vt"="2028-02-14";"@rm"="2栋业主" }
PI $cardSql @{ "@cn"="AC20260211001";"@oid"=11;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="2";"@vf"="2026-03-01";"@vt"="2028-02-28";"@rm"="2栋业主" }
PI $cardSql @{ "@cn"="AC20260211002";"@oid"=11;"@ct"="FAMILY";"@st"="SUSPENDED";"@bi"="2";"@vf"="2026-03-01";"@vt"="2027-02-28";"@rm"="家属卡-已挂失" }
PI $cardSql @{ "@cn"="AC20260212001";"@oid"=12;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="3";"@vf"="2026-03-10";"@vt"="2028-03-09";"@rm"="3栋业主" }
PI $cardSql @{ "@cn"="AC20260213001";"@oid"=13;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="3";"@vf"="2026-03-15";"@vt"="2028-03-14";"@rm"="3栋业主" }
PI $cardSql @{ "@cn"="AC20260214001";"@oid"=14;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="4";"@vf"="2026-04-01";"@vt"="2028-03-31";"@rm"="4栋业主" }
PI $cardSql @{ "@cn"="AC20260215001";"@oid"=15;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="4";"@vf"="2026-04-10";"@vt"="2028-04-09";"@rm"="4栋业主" }
PI $cardSql @{ "@cn"="AC20260216001";"@oid"=16;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="5";"@vf"="2026-04-20";"@vt"="2028-04-19";"@rm"="5栋业主" }
PI $cardSql @{ "@cn"="AC20260216002";"@oid"=16;"@ct"="FAMILY";"@st"="ACTIVE";"@bi"="5";"@vf"="2026-04-20";"@vt"="2027-10-19";"@rm"="家庭成员卡" }
PI $cardSql @{ "@cn"="AC20260217001";"@oid"=17;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="5";"@vf"="2026-05-01";"@vt"="2028-04-30";"@rm"="5栋业主" }
PI $cardSql @{ "@cn"="AC20260218001";"@oid"=18;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="6";"@vf"="2026-05-10";"@vt"="2028-05-09";"@rm"="6栋业主" }
PI $cardSql @{ "@cn"="AC20260219001";"@oid"=19;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="6";"@vf"="2026-05-15";"@vt"="2028-05-14";"@rm"="6栋业主" }
PI $cardSql @{ "@cn"="AC20260220001";"@oid"=20;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="1,2";"@vf"="2026-05-20";"@vt"="2028-05-19";"@rm"="多栋业主" }
PI $cardSql @{ "@cn"="AC20260221001";"@oid"=21;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="3,4";"@vf"="2026-05-25";"@vt"="2028-05-24";"@rm"="多栋业主" }
PI $cardSql @{ "@cn"="AC20260222001";"@oid"=22;"@ct"="OWNER";"@st"="ACTIVE";"@bi"="5,6";"@vf"="2026-06-01";"@vt"="2028-05-31";"@rm"="多栋业主" }
PI $cardSql @{ "@cn"="AC20260222002";"@oid"=22;"@ct"="VISITOR";"@st"="CANCELLED";"@bi"="5,6";"@vf"="2026-06-01";"@vt"="2026-07-01";"@rm"="访客卡-已注销" }
Write-Host "access_card: 22 cards inserted"

# === 2. access_log table already created, insert records ===
$logSql = "INSERT INTO access_log (card_id,card_no,user_id,user_name,direction,gate_location,building_id,access_time,access_status,deny_reason) VALUES (@ci,@cn,@ui,@un,@dir,@loc,@bid,@at,@as,@dr)"

PI $logSql @{ "@ci"=4;"@cn"="AC20260208001";"@ui"=8;"@un"="王明";"@dir"="IN";"@loc"="1栋大门";"@bid"=1;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=4;"@cn"="AC20260208001";"@ui"=8;"@un"="王明";"@dir"="OUT";"@loc"="1栋大门";"@bid"=1;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=4;"@cn"="AC20260208001";"@ui"=8;"@un"="王明";"@dir"="IN";"@loc"="小区南门";"@bid"=1;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=5;"@cn"="AC20260208002";"@ui"=8;"@un"="王明(家属)";"@dir"="IN";"@loc"="1栋大门";"@bid"=1;"@at"=RandDT(2);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=6;"@cn"="AC20260209001";"@ui"=9;"@un"="李华";"@dir"="IN";"@loc"="1栋大门";"@bid"=1;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=6;"@cn"="AC20260209001";"@ui"=9;"@un"="李华";"@dir"="OUT";"@loc"="1栋大门";"@bid"=1;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=7;"@cn"="AC20260210001";"@ui"=10;"@un"="赵阳";"@dir"="IN";"@loc"="2栋侧门";"@bid"=2;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=7;"@cn"="AC20260210001";"@ui"=10;"@un"="赵阳";"@dir"="OUT";"@loc"="2栋侧门";"@bid"=2;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=8;"@cn"="AC20260211001";"@ui"=11;"@un"="孙丽";"@dir"="IN";"@loc"="2栋大门";"@bid"=2;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=9;"@cn"="AC20260211002";"@ui"=11;"@un"="孙丽(家属)";"@dir"="IN";"@loc"="2栋大门";"@bid"=2;"@at"=RandDT(2);"@as"="DENIED";"@dr"="卡片已挂失" }
PI $logSql @{ "@ci"=10;"@cn"="AC20260212001";"@ui"=12;"@un"="周强";"@dir"="IN";"@loc"="3栋大门";"@bid"=3;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=10;"@cn"="AC20260212001";"@ui"=12;"@un"="周强";"@dir"="OUT";"@loc"="地下车库";"@bid"=3;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=11;"@cn"="AC20260213001";"@ui"=13;"@un"="吴敏";"@dir"="IN";"@loc"="3栋大门";"@bid"=3;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=11;"@cn"="AC20260213001";"@ui"=13;"@un"="吴敏";"@dir"="OUT";"@loc"="3栋大门";"@bid"=3;"@at"=RandDT(2);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=12;"@cn"="AC20260214001";"@ui"=14;"@un"="徐红";"@dir"="IN";"@loc"="4栋大门";"@bid"=4;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=13;"@cn"="AC20260215001";"@ui"=15;"@un"="高宇";"@dir"="IN";"@loc"="4栋大门";"@bid"=4;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=13;"@cn"="AC20260215001";"@ui"=15;"@un"="高宇";"@dir"="OUT";"@loc"="4栋大门";"@bid"=4;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=14;"@cn"="AC20260216001";"@ui"=16;"@un"="马娟";"@dir"="IN";"@loc"="5栋大门";"@bid"=5;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=15;"@cn"="AC20260216002";"@ui"=16;"@un"="马娟(家属)";"@dir"="IN";"@loc"="5栋大门";"@bid"=5;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=14;"@cn"="AC20260216001";"@ui"=16;"@un"="马娟";"@dir"="OUT";"@loc"="地下车库";"@bid"=5;"@at"=RandDT(2);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=16;"@cn"="AC20260217001";"@ui"=17;"@un"="黄磊";"@dir"="IN";"@loc"="5栋大门";"@bid"=5;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=16;"@cn"="AC20260217001";"@ui"=17;"@un"="黄磊";"@dir"="OUT";"@loc"="5栋侧门";"@bid"=5;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=17;"@cn"="AC20260218001";"@ui"=18;"@un"="林雪";"@dir"="IN";"@loc"="6栋大门";"@bid"=6;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=17;"@cn"="AC20260218001";"@ui"=18;"@un"="林雪";"@dir"="OUT";"@loc"="6栋大门";"@bid"=6;"@at"=RandDT(3);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=18;"@cn"="AC20260219001";"@ui"=19;"@un"="郑涛";"@dir"="IN";"@loc"="6栋大门";"@bid"=6;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=18;"@cn"="AC20260219001";"@ui"=19;"@un"="郑涛";"@dir"="OUT";"@loc"="小区北门";"@bid"=6;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=19;"@cn"="AC20260220001";"@ui"=20;"@un"="何苗";"@dir"="IN";"@loc"="1栋大门";"@bid"=1;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=19;"@cn"="AC20260220001";"@ui"=20;"@un"="何苗";"@dir"="IN";"@loc"="2栋大门";"@bid"=2;"@at"=RandDT(2);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=20;"@cn"="AC20260221001";"@ui"=21;"@un"="罗成";"@dir"="IN";"@loc"="3栋大门";"@bid"=3;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=20;"@cn"="AC20260221001";"@ui"=21;"@un"="罗成";"@dir"="OUT";"@loc"="4栋大门";"@bid"=4;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=21;"@cn"="AC20260222001";"@ui"=22;"@un"="宋娜";"@dir"="IN";"@loc"="5栋大门";"@bid"=5;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=22;"@cn"="AC20260222002";"@ui"=22;"@un"="宋娜(访客)";"@dir"="IN";"@loc"="6栋大门";"@bid"=6;"@at"=RandDT(1);"@as"="DENIED";"@dr"="卡片已注销" }
PI $logSql @{ "@ci"=21;"@cn"="AC20260222001";"@ui"=22;"@un"="宋娜";"@dir"="OUT";"@loc"="6栋大门";"@bid"=6;"@at"=RandDT(2);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=1;"@cn"="AC20260105001";"@ui"=5;"@un"="张伟";"@dir"="IN";"@loc"="物业中心";"@bid"=$null;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=1;"@cn"="AC20260105001";"@ui"=5;"@un"="张伟";"@dir"="OUT";"@loc"="物业中心";"@bid"=$null;"@at"=RandDT(0);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=2;"@cn"="AC20260106001";"@ui"=6;"@un"="刘芳";"@dir"="IN";"@loc"="小区南门";"@bid"=$null;"@at"=RandDT(1);"@as"="SUCCESS";"@dr"=$null }
PI $logSql @{ "@ci"=22;"@cn"="AC20260222002";"@ui"=22;"@un"="宋娜(访客)";"@dir"="IN";"@loc"="5栋大门";"@bid"=5;"@at"=RandDT(4);"@as"="DENIED";"@dr"="卡片已过期" }
Write-Host "access_log: 37 records inserted"

# Verify
$c = $conn.CreateCommand()
$c.CommandText = "SELECT 'access_card' AS tbl, COUNT(*) AS cnt FROM access_card UNION ALL SELECT 'access_log', COUNT(*) FROM access_log"
$r = $c.ExecuteReader()
Write-Host "`nFinal:"
while ($r.Read()) { Write-Host ("  " + $r["tbl"] + ": " + $r["cnt"]) }
$r.Close()
$conn.Close()
Write-Host "Done!"
