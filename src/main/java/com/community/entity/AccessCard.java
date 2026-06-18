package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("access_card")
public class AccessCard {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 卡片编号（唯一） */
    private String cardNo;
    /** 持卡业主ID */
    private Long ownerId;
    /** 卡片类型：OWNER-业主卡 / FAMILY-家庭卡 / VISITOR-访客卡 / TEMPORARY-临时卡 */
    private String cardType;
    /** 状态：ACTIVE-正常 / SUSPENDED-挂失冻结 / CANCELLED-注销 */
    private String status;
    /** 可通行楼栋ID，逗号分隔，如 "1,3,5"（空=全部） */
    private String buildingIds;
    /** 有效期起始 */
    private LocalDate validFrom;
    /** 有效期结束（null=永久） */
    private LocalDate validTo;
    /** 备注 */
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 联表展示字段 */
    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private String ownerPhone;
}
