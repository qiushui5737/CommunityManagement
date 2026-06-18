-- =============================================
-- 设施借用模块 - 建表 + 示例数据
-- 数据库：SQL Server
-- =============================================

-- 1. 设施表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'facility')
BEGIN
    CREATE TABLE facility (
        id            BIGINT IDENTITY(1,1) PRIMARY KEY,
        name          NVARCHAR(100)  NOT NULL,              -- 设施名称
        category      NVARCHAR(50)   NOT NULL,              -- 分类：运动器材/工具设备/文娱用品/清洁工具/其他
        description   NVARCHAR(500)  NULL,                  -- 描述
        image_url     NVARCHAR(500)  NULL,                  -- 图片路径
        location      NVARCHAR(200)  NULL,                  -- 存放位置
        deposit       DECIMAL(10,2)  DEFAULT 0,             -- 借用押金
        status        VARCHAR(20)    DEFAULT 'AVAILABLE',   -- 状态：AVAILABLE/BOOKED/MAINTENANCE/RETIRED
        create_time   DATETIME2      DEFAULT GETDATE(),
        update_time   DATETIME2      DEFAULT GETDATE()
    );
END
GO

-- 2. 借用申请表
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'facility_booking')
BEGIN
    CREATE TABLE facility_booking (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        facility_id     BIGINT         NOT NULL,              -- 设施 ID
        owner_id        BIGINT         NOT NULL,              -- 申请人（业主）ID
        purpose         NVARCHAR(500)  NULL,                  -- 借用事由
        duration_hours  INT            DEFAULT 24,            -- 预计借用时长（小时）
        status          VARCHAR(20)    DEFAULT 'PENDING',     -- PENDING/APPROVED/REJECTED/RETURNED
        handler_id      BIGINT         NULL,                  -- 审批人 ID
        reply_content   NVARCHAR(500)  NULL,                  -- 审批备注
        return_time     DATETIME2      NULL,                  -- 实际归还时间
        create_time     DATETIME2      DEFAULT GETDATE(),
        update_time     DATETIME2      DEFAULT GETDATE()
    );
END
GO

-- 3. 插入示例设施数据（15条，覆盖5个分类）
INSERT INTO facility (name, category, description, image_url, location, deposit, status) VALUES
-- 运动器材
(N'篮球',          N'运动器材', N'标准7号篮球，适合室内外场地使用。社区配有打气筒。',
 'https://img.icons8.com/color/200/basketball.png',                    N'物业前台',         0,   'AVAILABLE'),
(N'羽毛球拍套装',   N'运动器材', N'含2支球拍+1筒羽毛球（12只）。适合社区羽毛球场使用。',
 'https://img.icons8.com/color/200/badminton.png',                     N'物业前台',         0,   'AVAILABLE'),
(N'瑜伽垫',        N'运动器材', N'TPE材质加厚瑜伽垫（183cm×61cm），防滑耐用。',
 'https://img.icons8.com/color/200/yoga.png',                          N'物业前台',         0,   'AVAILABLE'),
(N'跳绳',          N'运动器材', N'可调节长度钢丝绳，适合日常锻炼。',
 'https://img.icons8.com/color/200/jump-rope.png',                     N'物业前台',         0,   'AVAILABLE'),

-- 工具设备
(N'电钻套装',      N'工具设备', N'博世12V锂电冲击钻，含钻头和螺丝批套装。适合家庭装修、挂画等。',
 'https://img.icons8.com/color/200/drill.png',                         N'物业工具房',       50,  'AVAILABLE'),
(N'梯子（人字梯）', N'工具设备', N'铝合金人字梯，展开高度2米，承重150kg。换灯泡、清洁高处必备。',
 'https://img.icons8.com/color/200/ladder.png',                        N'物业工具房',       0,   'AVAILABLE'),
(N'手推车',        N'工具设备', N'折叠式平板手推车，承重300kg。搬运家具、快递的好帮手。',
 'https://img.icons8.com/color/200/trolley.png',                       N'物业工具房',       0,   'AVAILABLE'),
(N'测距仪',        N'工具设备', N'激光测距仪，测量范围0.05~40米，精度±2mm。',
 'https://img.icons8.com/color/200/ruler.png',                         N'物业前台',         30,  'AVAILABLE'),

-- 文娱用品
(N'象棋',          N'文娱用品', N'实木中国象棋，含棋盘。可在社区活动室使用。',
 'https://img.icons8.com/color/200/chess.png',                         N'活动室',           0,   'AVAILABLE'),
(N'扑克牌套装',    N'文娱用品', N'含2副扑克牌，适合社区聚会娱乐。',
 'https://img.icons8.com/color/200/cards.png',                         N'活动室',           0,   'AVAILABLE'),
(N'投影仪',        N'文娱用品', N'1080P高清投影仪，支持HDMI/USB输入。可用于社区活动放映。',
 'https://img.icons8.com/color/200/projector.png',                     N'物业前台',         100, 'AVAILABLE'),

-- 清洁工具
(N'高压水枪',      N'清洁工具', N'家用高压清洗机，适合冲洗阳台、车位、自行车等。',
 'https://img.icons8.com/color/200/water-gun.png',                     N'物业工具房',       50,  'AVAILABLE'),
(N'蒸汽清洁机',    N'清洁工具', N'手持蒸汽清洁机，高温除菌，适合厨房油烟机、卫生间清洁。',
 'https://img.icons8.com/color/200/broom.png',                         N'物业工具房',       30,  'MAINTENANCE'),

-- 其他
(N'轮椅',          N'其他',     N'标准折叠轮椅，适合临时使用。承重100kg。',
 'https://img.icons8.com/color/200/wheelchair.png',                    N'物业前台',         0,   'AVAILABLE'),
(N'婴儿推车',      N'其他',     N'轻便折叠婴儿推车，适合0-3岁宝宝外出使用。',
 'https://img.icons8.com/color/200/baby-carriage.png',                 N'物业前台',         0,   'AVAILABLE');

PRINT N'✅ 设施借用模块建表 + 示例数据插入完成（15条设施）';
