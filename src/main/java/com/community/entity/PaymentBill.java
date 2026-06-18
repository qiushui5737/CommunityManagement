package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@TableName("payment_bill")
public class PaymentBill {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;       // 业主ID
    private Long houseId;       // 关联房屋ID
    private Long parkingSpaceId; // 关联车位ID（车位管理费专用）
    private Long feeItemId;     // 费用项目ID（如物业费、垃圾费）
    private BigDecimal amount;  // 金额
    private String status;      // PENDING-待缴, PAID-已缴, OVERDUE-逾期
    private LocalDate dueDate;  // 到期日
    private LocalDateTime payTime; // 支付时间
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String houseLabel;  // 房屋标签：楼栋-单元-房间号（后端组装）

    @TableField(exist = false)
    private String parkingLabel; // 车位标签：车位编号（后端组装）

    private static final DateTimeFormatter BILL_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 账单编号：格式 yyyyMMdd-P003（日期 + 业主编号）
     * 计算方法，无数据库字段，Jackson 自动序列化为 JSON 属性
     */
    public String getBillNo() {
        if (createTime == null || ownerId == null) return String.valueOf(id);
        return createTime.toLocalDate().format(BILL_NO_FMT) + "-P" + String.format("%03d", ownerId);
    }
}
