package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    /** SUGGESTION / COMPLAINT / INQUIRY */
    private String type;
    private String title;
    private String content;
    /** 逗号分隔的图片URL */
    private String images;
    /** PENDING / PROCESSING / REPLIED / CLOSED */
    private String status;
    private Long handlerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private Integer replyCount;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 反馈单号：格式 yyyyMMdd-F{业主ID 3位}{记录ID 3位}
     * 示例：20260609-F003001
     * 计算方法，无数据库字段，Jackson 自动序列化为 JSON 属性
     */
    public String getFeedbackNo() {
        if (createTime == null || ownerId == null) return String.valueOf(id);
        return createTime.toLocalDate().format(FMT) + "-F" + String.format("%03d", ownerId) + String.format("%03d", id);
    }
}
