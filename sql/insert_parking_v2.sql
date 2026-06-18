-- 每栋楼批量插入车位（不用游标，直接 INSERT）

-- 1栋 building_id=1, 4排×8列=32个
DECLARE @r INT, @c INT, @rand INT, @status NVARCHAR(20), @type NVARCHAR(20), @area DECIMAL(10,2), @price DECIMAL(10,2)

SET @r = 1
WHILE @r <= 4
BEGIN
    SET @c = 1
    WHILE @c <= 8
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE WHEN @rand < 40 THEN 'FREE' WHEN @rand < 75 THEN 'SOLD' WHEN @rand < 90 THEN 'LOCKED' ELSE 'RESERVED' END
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @type = CASE WHEN @rand < 60 THEN 'STANDARD' WHEN @rand < 80 THEN 'COMPACT' WHEN @rand < 95 THEN 'LARGE' ELSE 'VIP' END
        SET @area = CASE @type WHEN 'COMPACT' THEN 10.0 WHEN 'LARGE' THEN 15.0 WHEN 'VIP' THEN 18.0 ELSE 12.5 END
        SET @price = CASE @type WHEN 'COMPACT' THEN 60000 WHEN 'LARGE' THEN 100000 WHEN 'VIP' THEN 150000 ELSE 80000 END
        INSERT INTO parking_space (space_no,building_id,zone,row_no,col_no,area,type,status,price,create_time,update_time)
        VALUES ('A01-'+CAST(@r AS NVARCHAR)+RIGHT('0'+CAST(@c AS NVARCHAR),2), 1, N'1栋', @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())
        SET @c = @c + 1
    END
    SET @r = @r + 1
END

-- 2栋 building_id=2, 4排×8列=32个
SET @r = 1
WHILE @r <= 4
BEGIN
    SET @c = 1
    WHILE @c <= 8
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE WHEN @rand < 40 THEN 'FREE' WHEN @rand < 75 THEN 'SOLD' WHEN @rand < 90 THEN 'LOCKED' ELSE 'RESERVED' END
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @type = CASE WHEN @rand < 60 THEN 'STANDARD' WHEN @rand < 80 THEN 'COMPACT' WHEN @rand < 95 THEN 'LARGE' ELSE 'VIP' END
        SET @area = CASE @type WHEN 'COMPACT' THEN 10.0 WHEN 'LARGE' THEN 15.0 WHEN 'VIP' THEN 18.0 ELSE 12.5 END
        SET @price = CASE @type WHEN 'COMPACT' THEN 60000 WHEN 'LARGE' THEN 100000 WHEN 'VIP' THEN 150000 ELSE 80000 END
        INSERT INTO parking_space (space_no,building_id,zone,row_no,col_no,area,type,status,price,create_time,update_time)
        VALUES ('A02-'+CAST(@r AS NVARCHAR)+RIGHT('0'+CAST(@c AS NVARCHAR),2), 2, N'2栋', @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())
        SET @c = @c + 1
    END
    SET @r = @r + 1
END

-- 3栋 building_id=3, 5排×8列=40个
SET @r = 1
WHILE @r <= 5
BEGIN
    SET @c = 1
    WHILE @c <= 8
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE WHEN @rand < 40 THEN 'FREE' WHEN @rand < 75 THEN 'SOLD' WHEN @rand < 90 THEN 'LOCKED' ELSE 'RESERVED' END
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @type = CASE WHEN @rand < 60 THEN 'STANDARD' WHEN @rand < 80 THEN 'COMPACT' WHEN @rand < 95 THEN 'LARGE' ELSE 'VIP' END
        SET @area = CASE @type WHEN 'COMPACT' THEN 10.0 WHEN 'LARGE' THEN 15.0 WHEN 'VIP' THEN 18.0 ELSE 12.5 END
        SET @price = CASE @type WHEN 'COMPACT' THEN 60000 WHEN 'LARGE' THEN 100000 WHEN 'VIP' THEN 150000 ELSE 80000 END
        INSERT INTO parking_space (space_no,building_id,zone,row_no,col_no,area,type,status,price,create_time,update_time)
        VALUES ('B01-'+CAST(@r AS NVARCHAR)+RIGHT('0'+CAST(@c AS NVARCHAR),2), 3, N'3栋', @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())
        SET @c = @c + 1
    END
    SET @r = @r + 1
END

-- 4栋 building_id=4, 5排×8列=40个
SET @r = 1
WHILE @r <= 5
BEGIN
    SET @c = 1
    WHILE @c <= 8
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE WHEN @rand < 40 THEN 'FREE' WHEN @rand < 75 THEN 'SOLD' WHEN @rand < 90 THEN 'LOCKED' ELSE 'RESERVED' END
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @type = CASE WHEN @rand < 60 THEN 'STANDARD' WHEN @rand < 80 THEN 'COMPACT' WHEN @rand < 95 THEN 'LARGE' ELSE 'VIP' END
        SET @area = CASE @type WHEN 'COMPACT' THEN 10.0 WHEN 'LARGE' THEN 15.0 WHEN 'VIP' THEN 18.0 ELSE 12.5 END
        SET @price = CASE @type WHEN 'COMPACT' THEN 60000 WHEN 'LARGE' THEN 100000 WHEN 'VIP' THEN 150000 ELSE 80000 END
        INSERT INTO parking_space (space_no,building_id,zone,row_no,col_no,area,type,status,price,create_time,update_time)
        VALUES ('B02-'+CAST(@r AS NVARCHAR)+RIGHT('0'+CAST(@c AS NVARCHAR),2), 4, N'4栋', @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())
        SET @c = @c + 1
    END
    SET @r = @r + 1
END

-- 5栋 building_id=5, 6排×8列=48个
SET @r = 1
WHILE @r <= 6
BEGIN
    SET @c = 1
    WHILE @c <= 8
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE WHEN @rand < 40 THEN 'FREE' WHEN @rand < 75 THEN 'SOLD' WHEN @rand < 90 THEN 'LOCKED' ELSE 'RESERVED' END
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @type = CASE WHEN @rand < 60 THEN 'STANDARD' WHEN @rand < 80 THEN 'COMPACT' WHEN @rand < 95 THEN 'LARGE' ELSE 'VIP' END
        SET @area = CASE @type WHEN 'COMPACT' THEN 10.0 WHEN 'LARGE' THEN 15.0 WHEN 'VIP' THEN 18.0 ELSE 12.5 END
        SET @price = CASE @type WHEN 'COMPACT' THEN 60000 WHEN 'LARGE' THEN 100000 WHEN 'VIP' THEN 150000 ELSE 80000 END
        INSERT INTO parking_space (space_no,building_id,zone,row_no,col_no,area,type,status,price,create_time,update_time)
        VALUES ('C01-'+CAST(@r AS NVARCHAR)+RIGHT('0'+CAST(@c AS NVARCHAR),2), 5, N'5栋', @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())
        SET @c = @c + 1
    END
    SET @r = @r + 1
END

-- 6栋 building_id=6, 6排×8列=48个
SET @r = 1
WHILE @r <= 6
BEGIN
    SET @c = 1
    WHILE @c <= 8
    BEGIN
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @status = CASE WHEN @rand < 40 THEN 'FREE' WHEN @rand < 75 THEN 'SOLD' WHEN @rand < 90 THEN 'LOCKED' ELSE 'RESERVED' END
        SET @rand = ABS(CHECKSUM(NEWID())) % 100
        SET @type = CASE WHEN @rand < 60 THEN 'STANDARD' WHEN @rand < 80 THEN 'COMPACT' WHEN @rand < 95 THEN 'LARGE' ELSE 'VIP' END
        SET @area = CASE @type WHEN 'COMPACT' THEN 10.0 WHEN 'LARGE' THEN 15.0 WHEN 'VIP' THEN 18.0 ELSE 12.5 END
        SET @price = CASE @type WHEN 'COMPACT' THEN 60000 WHEN 'LARGE' THEN 100000 WHEN 'VIP' THEN 150000 ELSE 80000 END
        INSERT INTO parking_space (space_no,building_id,zone,row_no,col_no,area,type,status,price,create_time,update_time)
        VALUES ('C02-'+CAST(@r AS NVARCHAR)+RIGHT('0'+CAST(@c AS NVARCHAR),2), 6, N'6栋', @r, @c, @area, @type, @status, @price, GETDATE(), GETDATE())
        SET @c = @c + 1
    END
    SET @r = @r + 1
END

-- 验证
SELECT building_id, COUNT(*) AS cnt FROM parking_space GROUP BY building_id ORDER BY building_id
