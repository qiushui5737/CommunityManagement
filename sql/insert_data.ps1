<#  Use UTF-8 BOM encoding for this script  #>
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$conn = New-Object System.Data.SqlClient.SqlConnection
$conn.ConnectionString = "Server=localhost;Database=community_db;User Id=Community;Password=20040901mhh;TrustServerCertificate=true"
$conn.Open()

function ParamInsert($sql, $params) {
    $c = $conn.CreateCommand()
    $c.CommandText = $sql
    foreach ($k in $params.Keys) {
        $p = New-Object System.Data.SqlClient.SqlParameter($k, $params[$k])
        $c.Parameters.Add($p) | Out-Null
    }
    $c.ExecuteNonQuery() | Out-Null
}

function Exec($sql) {
    $c = $conn.CreateCommand()
    $c.CommandText = $sql
    $c.ExecuteNonQuery() | Out-Null
}

# Clean
Exec "DELETE FROM feedback_reply"
Exec "DELETE FROM feedback"
Exec "DELETE FROM access_card"
Write-Host "Cleaned"

# ====== access_card ======
ParamInsert "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES ('AC20260601001',3,'OWNER','ACTIVE','1,2','2026-01-01','2027-12-31',@r)" @{ "@r" = "业主主卡" }
ParamInsert "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES ('AC20260601002',3,'FAMILY','ACTIVE','1,2','2026-01-01','2027-06-30',@r)" @{ "@r" = "家属卡" }
ParamInsert "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES ('AC20260601003',4,'OWNER','ACTIVE','3,4,5','2026-03-01','2028-02-28',@r)" @{ "@r" = "业主主卡" }
ParamInsert "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES ('AC20260601004',4,'VISITOR','SUSPENDED','3','2026-05-01','2026-06-30',@r)" @{ "@r" = "访客临时卡-已挂失" }
ParamInsert "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES ('AC20260601005',3,'TEMPORARY','CANCELLED','1','2026-04-01','2026-05-01',@r)" @{ "@r" = "临时卡-已注销" }
ParamInsert "INSERT INTO access_card (card_no,owner_id,card_type,status,building_ids,valid_from,valid_to,remark) VALUES ('AC20260601006',4,'FAMILY','ACTIVE','3,4','2026-06-01','2027-05-31',@r)" @{ "@r" = "家庭成员卡" }
Write-Host "access_card: 6 done"

# ====== feedback ======
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,handler_id,create_time,update_time) VALUES (3,'SUGGESTION',@t,@c,'REPLIED',1,'2026-05-10 09:30:00','2026-05-12 14:00:00')" @{ "@t" = "建议增加小区绿化面积"; "@c" = "希望物业能在3栋和4栋之间增加绿化带和休息座椅，提升居住环境品质。" }
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,handler_id,create_time,update_time) VALUES (3,'COMPLAINT',@t,@c,'PROCESSING',1,'2026-05-15 18:20:00','2026-05-16 10:00:00')" @{ "@t" = "地下车库灯光太暗"; "@c" = "地下车库B2层灯光非常暗，存在安全隐患，请尽快处理。" }
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,handler_id,create_time,update_time) VALUES (4,'INQUIRY',@t,@c,'REPLIED',2,'2026-05-20 11:00:00','2026-05-21 09:30:00')" @{ "@t" = "物业费缴纳方式咨询"; "@c" = "请问支持哪些物业费缴纳方式？是否可以通过微信或支付宝在线缴费？" }
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,create_time,update_time) VALUES (3,'SUGGESTION',@t,@c,'PENDING','2026-06-01 08:00:00','2026-06-01 08:00:00')" @{ "@t" = "建议开放快递柜服务"; "@c" = "小区目前没有快递柜，取件非常不便，建议设置智能快递柜。" }
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,handler_id,create_time,update_time) VALUES (4,'COMPLAINT',@t,@c,'PROCESSING',1,'2026-06-03 16:45:00','2026-06-04 09:00:00')" @{ "@t" = "5栋电梯经常故障"; "@c" = "5栋2号电梯近一个月内已故障4次，严重影响居民出行，请彻底检修。" }
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,create_time,update_time) VALUES (3,'INQUIRY',@t,@c,'PENDING','2026-06-05 13:15:00','2026-06-05 13:15:00')" @{ "@t" = "停车位租赁咨询"; "@c" = "请问目前小区还有可租赁的停车位吗？月租费用是多少？" }
ParamInsert "INSERT INTO feedback (owner_id,type,title,content,status,handler_id,create_time,update_time) VALUES (4,'SUGGESTION',@t,@c,'CLOSED',2,'2026-04-20 10:00:00','2026-05-01 15:00:00')" @{ "@t" = "希望增加儿童游乐设施"; "@c" = "小区内儿童较多但没有游乐设施，建议在中心花园增设儿童游乐区。" }
Write-Host "feedback: 7 done"

# ====== feedback_reply ======
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (1,1,'ADMIN',@c,'2026-05-11 10:00:00')" @{ "@c" = "感谢您的建议！绿化改造方案已提交业委会审批，预计下季度启动。" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (1,3,'OWNER',@c,'2026-05-12 14:00:00')" @{ "@c" = "太好了，期待改造效果！" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (2,1,'ADMIN',@c,'2026-05-16 10:00:00')" @{ "@c" = "已安排工程部排查，初步判断是灯管老化，本周内完成更换。" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (3,2,'ADMIN',@c,'2026-05-20 15:00:00')" @{ "@c" = "目前支持：1.物业中心现场缴费；2.微信公众号在线缴费（微信/支付宝）；3.银行转账。" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (3,4,'OWNER',@c,'2026-05-21 09:30:00')" @{ "@c" = "好的，谢谢回复！" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (5,1,'ADMIN',@c,'2026-06-04 09:00:00')" @{ "@c" = "非常抱歉！已联系电梯维保单位全面检修，本周五前完成。" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (7,2,'ADMIN',@c,'2026-04-25 11:00:00')" @{ "@c" = "经业委会讨论通过，已批准增设儿童游乐设施，预计7月完工。" }
ParamInsert "INSERT INTO feedback_reply (feedback_id,user_id,user_role,content,create_time) VALUES (7,4,'OWNER',@c,'2026-04-26 09:00:00')" @{ "@c" = "太棒了，孩子们一定很开心！" }
Write-Host "feedback_reply: 8 done"

# Verify
$c = $conn.CreateCommand()
$c.CommandText = "SELECT 'access_card' AS tbl, COUNT(*) AS cnt FROM access_card UNION ALL SELECT 'feedback', COUNT(*) FROM feedback UNION ALL SELECT 'feedback_reply', COUNT(*) FROM feedback_reply"
$reader = $c.ExecuteReader()
Write-Host "`nFinal:"
while ($reader.Read()) { Write-Host ("  " + $reader["tbl"] + ": " + $reader["cnt"]) }
$reader.Close()
$conn.Close()
Write-Host "All done!"
