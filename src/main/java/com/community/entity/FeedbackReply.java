package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback_reply")
public class FeedbackReply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long feedbackId;
    private Long userId;
    /** OWNER / ADMIN */
    private String userRole;
    private String content;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String userName;
}
