package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("parking_space")
public class ParkingSpace {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String spaceNo;       // 车位编号
    private Long buildingId;      // 所属楼栋ID
    private String zone;          // 区域（如 A区、B区，保留兼容）
    private Integer rowNo;        // 行号（可视化布局用）
    private Integer colNo;        // 列号（可视化布局用）
    private BigDecimal area;      // 面积（㎡）
    private String type;          // 类型：STANDARD-标准 / COMPACT-微型 / LARGE-大型 / VIP-VIP
    private String status;        // 状态：FREE-待售 / LOCKED-锁定 / SOLD-已售 / RESERVED-已预订
    private Long ownerId;         // 购买业主ID
    private BigDecimal price;     // 挂牌价格
    private BigDecimal purchasePrice; // 实际成交价格
    private LocalDateTime purchaseTime; // 购买时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 联表查询展示字段（不映射数据库列）
    @TableField(exist = false)
    private String ownerName;     // 业主姓名
    @TableField(exist = false)
    private String ownerPhone;    // 业主电话
    @TableField(exist = false)
    private String buildingName;  // 楼栋名称
    @TableField(exist = false)
    private String buildingNo;    // 楼栋编号
}
