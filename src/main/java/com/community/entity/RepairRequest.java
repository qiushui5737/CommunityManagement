package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@TableName("repair_request")
public class RepairRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private String category;
    private String title;
    private String description;
    private String imageUrl;
    private String status;
    private Long handlerId;
    private String replyContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableField(exist = false)
    private String ownerName;  // 用于接收联表查询到的真实姓名

    private static final DateTimeFormatter REPAIR_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 报修单号：格式 yyyyMMdd-R003（日期 + 业主编号）
     * 计算方法，无数据库字段，Jackson 自动序列化
     */
    public String getRepairNo() {
        if (createTime == null || ownerId == null) return String.valueOf(id);
        return createTime.toLocalDate().format(REPAIR_NO_FMT) + "-R" + String.format("%03d", ownerId);
    }
}
