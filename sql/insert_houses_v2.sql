-- =============================================
-- 批量生成房屋数据：每层每单元 4 户
-- 房间号格式：楼层号+01/02/03/04（如 301, 302, 303, 304）
-- =============================================

DECLARE @unitId INT, @maxFloor INT, @floor INT, @room INT
DECLARE @roomNo NVARCHAR(10), @status NVARCHAR(20), @rand INT, @area DECIMAL(10,2)
DECLARE @roomsPerFloor INT = 4

-- 遍历所有单元
DECLARE unitCursor CURSOR FOR
    SELECT u.id, b.total_floors
    FROM community_unit u
    JOIN community_building b ON u.building_id = b.id

OPEN unitCursor
FETCH NEXT FROM unitCursor INTO @unitId, @maxFloor

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @floor = 1
    WHILE @floor <= @maxFloor
    BEGIN
        SET @room = 1
        WHILE @room <= @roomsPerFloor
        BEGIN
            -- 房间号：楼层 + 两位序号（如 101, 1203）
            IF @floor < 10
                SET @roomNo = CAST(@floor AS NVARCHAR) + RIGHT('0' + CAST(@room AS NVARCHAR), 2)
            ELSE
                SET @roomNo = CAST(@floor AS NVARCHAR) + RIGHT('0' + CAST(@room AS NVARCHAR), 2)

            -- 随机状态
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @status = CASE
                WHEN @rand < 50 THEN 'OCCUPIED'
                WHEN @rand < 72 THEN 'RENTED'
                ELSE 'VACANT'
            END

            -- 面积：根据房间号尾数不同给不同面积
            SET @area = CASE @room
                WHEN 1 THEN 89.50 + (ABS(CHECKSUM(NEWID())) % 200) / 10.0
                WHEN 2 THEN 105.30 + (ABS(CHECKSUM(NEWID())) % 200) / 10.0
                WHEN 3 THEN 78.60 + (ABS(CHECKSUM(NEWID())) % 200) / 10.0
                WHEN 4 THEN 120.00 + (ABS(CHECKSUM(NEWID())) % 300) / 10.0
            END

            INSERT INTO community_house (unit_id, room_no, area, status, create_time)
            VALUES (@unitId, @roomNo, @area, @status, GETDATE())

            SET @room = @room + 1
        END
        SET @floor = @floor + 1
    END
    FETCH NEXT FROM unitCursor INTO @unitId, @maxFloor
END

CLOSE unitCursor
DEALLOCATE unitCursor

-- 验证结果
SELECT b.building_no AS no, b.name, b.total_floors AS floors,
    (SELECT COUNT(*) FROM community_unit WHERE building_id = b.id) AS units,
    COUNT(h.id) AS rooms,
    SUM(CASE WHEN h.status='OCCUPIED' THEN 1 ELSE 0 END) AS occupied,
    SUM(CASE WHEN h.status='RENTED' THEN 1 ELSE 0 END) AS rented,
    SUM(CASE WHEN h.status='VACANT' THEN 1 ELSE 0 END) AS vacant
FROM community_building b
JOIN community_unit u ON u.building_id = b.id
JOIN community_house h ON h.unit_id = u.id
GROUP BY b.id, b.building_no, b.name, b.total_floors
ORDER BY b.id;
