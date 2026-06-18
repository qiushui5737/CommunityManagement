-- =============================================
-- 批量插入楼栋、单元、房屋数据
-- =============================================

-- 新增 4 栋楼（ID 3-6）
INSERT INTO community_building (building_no, name, total_floors, create_time) VALUES
('B01', N'3栋', 15, GETDATE()),
('B02', N'4栋', 20, GETDATE()),
('C01', N'5栋', 12, GETDATE()),
('C02', N'6栋', 25, GETDATE());

-- 获取新楼栋ID并插入单元
-- 3栋 (building_id=3): 3个单元
INSERT INTO community_unit (building_id, unit_no, create_time) VALUES
(3, N'1单元', GETDATE()),
(3, N'2单元', GETDATE()),
(3, N'3单元', GETDATE());

-- 4栋 (building_id=4): 2个单元
INSERT INTO community_unit (building_id, unit_no, create_time) VALUES
(4, N'1单元', GETDATE()),
(4, N'2单元', GETDATE());

-- 5栋 (building_id=5): 2个单元
INSERT INTO community_unit (building_id, unit_no, create_time) VALUES
(5, N'1单元', GETDATE()),
(5, N'2单元', GETDATE());

-- 6栋 (building_id=6): 3个单元
INSERT INTO community_unit (building_id, unit_no, create_time) VALUES
(6, N'1单元', GETDATE()),
(6, N'2单元', GETDATE()),
(6, N'3单元', GETDATE());

-- =============================================
-- 批量插入房屋数据（用存储过程循环生成）
-- =============================================

-- 1栋 (building_id=1, 18层, 单元ID: 1,2)
-- 每层每单元2户，共 18*2*2 = 72户
DECLARE @floor INT, @unit INT, @room INT, @status NVARCHAR(20), @rand INT
DECLARE @unitId BIGINT, @area DECIMAL(10,2)

-- 1栋1单元 (unit_id=1): 1-18层
SET @floor = 1
WHILE @floor <= 18
BEGIN
    SET @room = 1
    WHILE @room <= 2
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE
            WHEN @rand < 55 THEN 'OCCUPIED'
            WHEN @rand < 75 THEN 'RENTED'
            ELSE 'VACANT'
        END
        SET @area = CASE WHEN @room = 1 THEN 89.50 ELSE 120.30 END
        INSERT INTO community_house (unit_id, room_no, area, status, create_time)
        VALUES (1, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
        SET @room = @room + 1
    END
    SET @floor = @floor + 1
END

-- 1栋2单元 (unit_id=2): 1-18层
SET @floor = 1
WHILE @floor <= 18
BEGIN
    SET @room = 1
    WHILE @room <= 2
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE
            WHEN @rand < 50 THEN 'OCCUPIED'
            WHEN @rand < 70 THEN 'RENTED'
            ELSE 'VACANT'
        END
        SET @area = CASE WHEN @room = 1 THEN 95.00 ELSE 110.80 END
        INSERT INTO community_house (unit_id, room_no, area, status, create_time)
        VALUES (2, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
        SET @room = @room + 1
    END
    SET @floor = @floor + 1
END

-- 2栋 (building_id=2, 22层, 单元ID: 3,4)
-- 2栋1单元 (unit_id=3)
SET @floor = 1
WHILE @floor <= 22
BEGIN
    SET @room = 1
    WHILE @room <= 2
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE
            WHEN @rand < 60 THEN 'OCCUPIED'
            WHEN @rand < 80 THEN 'RENTED'
            ELSE 'VACANT'
        END
        SET @area = CASE WHEN @room = 1 THEN 78.60 ELSE 135.20 END
        INSERT INTO community_house (unit_id, room_no, area, status, create_time)
        VALUES (3, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
        SET @room = @room + 1
    END
    SET @floor = @floor + 1
END

-- 2栋2单元 (unit_id=4)
SET @floor = 1
WHILE @floor <= 22
BEGIN
    SET @room = 1
    WHILE @room <= 2
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE
            WHEN @rand < 45 THEN 'OCCUPIED'
            WHEN @rand < 70 THEN 'RENTED'
            ELSE 'VACANT'
        END
        SET @area = CASE WHEN @room = 1 THEN 100.00 ELSE 88.50 END
        INSERT INTO community_house (unit_id, room_no, area, status, create_time)
        VALUES (4, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
        SET @room = @room + 1
    END
    SET @floor = @floor + 1
END

-- 3栋 (building_id=3, 15层, 单元ID: 5,6,7)
DECLARE @u INT = 5
WHILE @u <= 7
BEGIN
    SET @floor = 1
    WHILE @floor <= 15
    BEGIN
        SET @room = 1
        WHILE @room <= 2
        BEGIN
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @status = CASE
                WHEN @rand < 50 THEN 'OCCUPIED'
                WHEN @rand < 72 THEN 'RENTED'
                ELSE 'VACANT'
            END
            SET @area = 85.00 + (ABS(CHECKSUM(NEWID())) % 500) / 10.0
            INSERT INTO community_house (unit_id, room_no, area, status, create_time)
            VALUES (@u, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
            SET @room = @room + 1
        END
        SET @floor = @floor + 1
    END
    SET @u = @u + 1
END

-- 4栋 (building_id=4, 20层, 单元ID: 8,9)
SET @u = 8
WHILE @u <= 9
BEGIN
    SET @floor = 1
    WHILE @floor <= 20
    BEGIN
        SET @room = 1
        WHILE @room <= 2
        BEGIN
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @status = CASE
                WHEN @rand < 65 THEN 'OCCUPIED'
                WHEN @rand < 82 THEN 'RENTED'
                ELSE 'VACANT'
            END
            SET @area = 92.00 + (ABS(CHECKSUM(NEWID())) % 400) / 10.0
            INSERT INTO community_house (unit_id, room_no, area, status, create_time)
            VALUES (@u, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
            SET @room = @room + 1
        END
        SET @floor = @floor + 1
    END
    SET @u = @u + 1
END

-- 5栋 (building_id=5, 12层, 单元ID: 10,11)
SET @u = 10
WHILE @u <= 11
BEGIN
    SET @floor = 1
    WHILE @floor <= 12
    BEGIN
        SET @room = 1
        WHILE @room <= 3
        BEGIN
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @status = CASE
                WHEN @rand < 40 THEN 'OCCUPIED'
                WHEN @rand < 65 THEN 'RENTED'
                ELSE 'VACANT'
            END
            SET @area = 70.00 + (ABS(CHECKSUM(NEWID())) % 600) / 10.0
            INSERT INTO community_house (unit_id, room_no, area, status, create_time)
            VALUES (@u, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
            SET @room = @room + 1
        END
        SET @floor = @floor + 1
    END
    SET @u = @u + 1
END

-- 6栋 (building_id=6, 25层, 单元ID: 12,13,14)
SET @u = 12
WHILE @u <= 14
BEGIN
    SET @floor = 1
    WHILE @floor <= 25
    BEGIN
        SET @room = 1
        WHILE @room <= 2
        BEGIN
            SET @rand = ABS(CHECKSUM(NEWID())) % 100
            SET @status = CASE
                WHEN @rand < 70 THEN 'OCCUPIED'
                WHEN @rand < 85 THEN 'RENTED'
                ELSE 'VACANT'
            END
            SET @area = 88.00 + (ABS(CHECKSUM(NEWID())) % 500) / 10.0
            INSERT INTO community_house (unit_id, room_no, area, status, create_time)
            VALUES (@u, CAST(@floor AS NVARCHAR) + '0' + CAST(@room AS NVARCHAR), @area, @status, GETDATE())
            SET @room = @room + 1
        END
        SET @floor = @floor + 1
    END
    SET @u = @u + 1
END

-- 验证结果
SELECT b.name AS building, COUNT(h.id) AS rooms,
    SUM(CASE WHEN h.status='OCCUPIED' THEN 1 ELSE 0 END) AS occupied,
    SUM(CASE WHEN h.status='RENTED' THEN 1 ELSE 0 END) AS rented,
    SUM(CASE WHEN h.status='VACANT' THEN 1 ELSE 0 END) AS vacant
FROM community_building b
JOIN community_unit u ON u.building_id = b.id
JOIN community_house h ON h.unit_id = u.id
GROUP BY b.name
ORDER BY b.name;
