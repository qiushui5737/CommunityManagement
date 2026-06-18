package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设施借用申请
 */
@Data
@TableName("facility_booking")
public class FacilityBooking {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设施 ID */
    private Long facilityId;
    /** 申请人（业主）ID */
    private Long ownerId;
    /** 借用事由 */
    private String purpose;
    /** 预计借用时长（小时） */
    private Integer durationHours;
    /** 申请状态：PENDING=待审批 / APPROVED=已通过 / REJECTED=已驳回 / RETURNED=已归还 */
    private String status;
    /** 审批人 ID */
    private Long handlerId;
    /** 审批备注 */
    private String replyContent;
    /** 实际归还时间 */
    private LocalDateTime returnTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ====== 展示字段（不在数据库表中） ======
    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private String ownerPhone;
    @TableField(exist = false)
    private String facilityName;
    @TableField(exist = false)
    private String facilityImage;
}
