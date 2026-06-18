-- =============================================
-- 社区公告表 announcement
-- =============================================
CREATE TABLE announcement (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    title        NVARCHAR(200)  NOT NULL,           -- 公告标题
    content      NVARCHAR(MAX)  NOT NULL,           -- 公告内容
    type         NVARCHAR(20)   DEFAULT 'NOTICE',   -- 公告类型：NOTICE/ACTIVITY/MAINTENANCE/OTHER
    publisher_id BIGINT         NOT NULL,           -- 发布人ID（关联 sys_user.id）
    status       NVARCHAR(20)   DEFAULT 'PUBLISHED',-- 状态：DRAFT/PUBLISHED/WITHDRAWN
    is_top       INT            DEFAULT 0,          -- 是否置顶：0-否, 1-是
    create_time  DATETIME2      DEFAULT GETDATE(),  -- 创建时间
    update_time  DATETIME2      DEFAULT GETDATE(),  -- 更新时间
    CONSTRAINT FK_announcement_user FOREIGN KEY (publisher_id) REFERENCES sys_user(id)
);

-- 索引：按状态和创建时间查询（最常用场景）
CREATE INDEX idx_announcement_status_time ON announcement(status, create_time DESC);
-- 索引：按类型查询
CREATE INDEX idx_announcement_type ON announcement(type);
