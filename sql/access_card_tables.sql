-- 门禁卡模块建表 (SQL Server)
IF OBJECT_ID('access_card', 'U') IS NOT NULL DROP TABLE access_card;

CREATE TABLE access_card (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    card_no      NVARCHAR(50) NOT NULL UNIQUE,
    owner_id     BIGINT NOT NULL,
    card_type    NVARCHAR(20) NOT NULL,         -- OWNER / FAMILY / VISITOR / TEMPORARY
    status       NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / SUSPENDED / CANCELLED
    building_ids NVARCHAR(500),                 -- 可通行楼栋ID，逗号分隔
    valid_from   DATE,
    valid_to     DATE,
    remark       NVARCHAR(200),
    create_time  DATETIME2 NOT NULL DEFAULT GETDATE(),
    update_time  DATETIME2
);

CREATE INDEX idx_card_owner ON access_card(owner_id);
CREATE INDEX idx_card_status ON access_card(status);
CREATE INDEX idx_card_type ON access_card(card_type);

PRINT 'access_card table created successfully';
