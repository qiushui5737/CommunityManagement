package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("payment_bill")
public class PaymentBill {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;       // 业主ID
    private Long feeItemId;     // 费用项目ID（如物业费、垃圾费）
    private BigDecimal amount;  // 金额
    private String status;      // PENDING-待缴, PAID-已缴, OVERDUE-逾期
    private LocalDate dueDate;  // 到期日
    private LocalDateTime payTime; // 支付时间
    private LocalDateTime createTime;
}
