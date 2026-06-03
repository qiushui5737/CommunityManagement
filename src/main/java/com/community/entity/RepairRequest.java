package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

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
    @TableField(exist = false) // 告诉 MyBatis-Plus 这个字段不在数据库表里
    private String ownerName;  // 用于接收联表查询到的真实姓名
}
