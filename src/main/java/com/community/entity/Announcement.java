package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 公告类型：NOTICE-通知, ACTIVITY-活动, MAINTENANCE-维护, OTHER-其他 */
    private String type;

    /** 发布人ID */
    private Long publisherId;

    /** 状态：DRAFT-草稿, PUBLISHED-已发布, WITHDRAWN-已撤回 */
    private String status;

    /** 是否置顶：0-否, 1-是 */
    private Integer isTop;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联表查询展示字段 —— 发布人姓名 */
    @TableField(exist = false)
    private String publisherName;
}
