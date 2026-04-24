package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("property_fee_item")
public class PropertyFeeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemName;      // 项目名称（如：物业管理费）
    private BigDecimal amount;    // 标准金额
    private String cycle;         // 周期：MONTH/QUARTER/YEAR
    private Integer status;       // 1启用 0停用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
