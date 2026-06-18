-- =============================================
-- 车位数据与楼栋对接：添加 building_id 并批量插入数据
-- =============================================

-- 1. 清空旧数据
DELETE FROM parking_space;

-- 2. 添加 building_id 列（如果不存在）
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('parking_space') AND name = 'building_id')
BEGIN
    ALTER TABLE parking_space ADD building_id BIGINT NULL;
END
GO

-- 3. 批量生成车位数据：每栋楼一个独立停车区
-- 每栋楼约 20-40 个车位，排列成 4-5 行 × 6-8 列
DECLARE @buildingId BIGINT, @buildingNo NVARCHAR(20), @bName NVARCHAR(50)
DECLARE @maxRow INT, @maxCol INT, @r INT, @c INT
DECLARE @spaceNo NVARCHAR(20), @status NVARCHAR(20), @type NVARCHAR(20)
DECLARE @area DECIMAL(10,2), @price DECIMAL(10,2), @rand INT

DECLARE bCursor CURSOR FOR
    SELECT id, building_no, name FROM community_building

OPEN bCursor
FETCH NEXT FROM bCursor INTO @buildingId, @buildingNo, @bName

WHILE @@FETCH_STATUS = 0
BEGIN
    -- 根据楼栋决定车位规模
    SET @maxRow = CASE
        WHEN @buildingId <= 2 THEN 4   -- 1栋、2栋：4排
        WHEN @buildingId <= 4 THEN 5   -- 3栋、4栋：5排
        ELSE 6                          -- 5栋、6栋：6排
    END
    SET @maxCol = 8  -- 每排8个车位

    SET @r = 1
    WHILE @r <= @maxRow
    BEGIN
        SET @c = 1
        WHILE @c <= @maxCol
        BEGIN
            -- 编号：楼栋号-排号+列号（如 A01-101, A01-208）
            SET @spaceNo = @buildingNo + N'-' + CAST(@r AS NVARCHAR) + RIGHT('0' + CAST(@c AS NVARCHAR), 2)

            -- 随机状态
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @status = CASE
                WHEN @rand < 40 THEN 'FREE'
                WHEN @rand < 75 THEN 'SOLD'
                WHEN @rand < 90 THEN 'LOCKED'
                ELSE 'RESERVED'
            END

            -- 随机类型
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @type = CASE
                WHEN @rand < 60 THEN 'STANDARD'
                WHEN @rand < 80 THEN 'COMPACT'
                WHEN @rand < 95 THEN 'LARGE'
                ELSE 'VIP'
            END

            -- 面积和价格
            SET @area = CASE @type
                WHEN 'COMPACT' THEN 10.0 + (ABS(CHECKSUM(NEWID())) % 20) / 10.0
                WHEN 'LARGE' THEN 15.0 + (ABS(CHECKSUM(NEWID())) % 30) / 10.0
                WHEN 'VIP' THEN 18.0 + (ABS(CHECKSUM(NEWID())) % 40) / 10.0
                ELSE 12.5 + (ABS(CHECKSUM(NEWID())) % 25) / 10.0
            END
            SET @price = CASE @type
                WHEN 'COMPACT' THEN 60000 + (ABS(CHECKSUM(NEWID())) % 20000)
                WHEN 'LARGE' THEN 100000 + (ABS(CHECKSUM(NEWID())) % 30000)
                WHEN 'VIP' THEN 150000 + (ABS(CHECKSUM(NEWID())) % 50000)
                ELSE 80000 + (ABS(CHECKSUM(NEWID())) % 30000)
            END

            INSERT INTO parking_space (space_no, building_id, zone, row_no, col_no, area, type, status, price, create_time, update_time)
            VALUES (@spaceNo, @buildingId, @bName, @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())

            SET @c = @c + 1
        END
        SET @r = @r + 1
    END

    FETCH NEXT FROM bCursor INTO @buildingId, @buildingNo, @bName
END

CLOSE bCursor
DEALLOCATE bCursor

-- 4. 验证
SELECT b.building_no, b.name, COUNT(p.id) AS spaces,
    SUM(CASE WHEN p.status='FREE' THEN 1 ELSE 0 END) AS free_cnt,
    SUM(CASE WHEN p.status='SOLD' THEN 1 ELSE 0 END) AS sold_cnt,
    SUM(CASE WHEN p.status='LOCKED' THEN 1 ELSE 0 END) AS locked_cnt,
    SUM(CASE WHEN p.status='RESERVED' THEN 1 ELSE 0 END) AS reserved_cnt
FROM community_building b
LEFT JOIN parking_space p ON p.building_id = b.id
GROUP BY b.building_no, b.name
ORDER BY b.id;
