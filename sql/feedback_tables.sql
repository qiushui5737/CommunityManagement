-- =============================================
-- 留言反馈模块 - 建表脚本 (SQL Server)
-- =============================================

-- 1. 反馈主表
IF OBJECT_ID('feedback', 'U') IS NOT NULL DROP TABLE feedback;

CREATE TABLE feedback (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    owner_id    BIGINT NOT NULL,
    type        NVARCHAR(20) NOT NULL,       -- SUGGESTION / COMPLAINT / INQUIRY
    title       NVARCHAR(100) NOT NULL,
    content     NVARCHAR(2000) NOT NULL,
    status      NVARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING / PROCESSING / REPLIED / CLOSED
    handler_id  BIGINT,
    create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
    update_time DATETIME2
);

-- 2. 反馈回复表
IF OBJECT_ID('feedback_reply', 'U') IS NOT NULL DROP TABLE feedback_reply;

CREATE TABLE feedback_reply (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    feedback_id  BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    user_role    NVARCHAR(20) NOT NULL,      -- OWNER / ADMIN
    content      NVARCHAR(2000) NOT NULL,
    create_time  DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 3. 索引
CREATE INDEX idx_feedback_owner ON feedback(owner_id);
CREATE INDEX idx_feedback_status ON feedback(status);
CREATE INDEX idx_feedback_type ON feedback(type);
CREATE INDEX idx_reply_feedback ON feedback_reply(feedback_id);

PRINT 'feedback & feedback_reply tables created successfully';
