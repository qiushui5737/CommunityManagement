package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.Result;
import com.community.entity.PaymentBill;
import com.community.service.PaymentBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/owner/bills")
@RequiredArgsConstructor
public class PaymentBillController {
    private final PaymentBillService billService;

    // 1. 查询当前业主账单（支持按状态筛选）
    @GetMapping
    public Result<List<PaymentBill>> list(@RequestAttribute("userId") Long ownerId,
                                          @RequestParam(required = false) String status) {
        LambdaQueryWrapper<PaymentBill> qw = new LambdaQueryWrapper<>();
        qw.eq(PaymentBill::getOwnerId, ownerId);

        // 👇 关键修复：增加非空和 ALL 判断，并转为大写防止大小写不匹配
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            qw.eq(PaymentBill::getStatus, status.toUpperCase());
        }

        qw.orderByDesc(PaymentBill::getCreateTime);
        return Result.ok(billService.list(qw));
    }


    // 2. 模拟在线支付
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, @RequestAttribute("userId") Long ownerId) {
        PaymentBill bill = billService.getById(id);
        if (bill == null || !bill.getOwnerId().equals(ownerId)) {
            return Result.error(403, "无权操作该账单");
        }
        if (!"PENDING".equals(bill.getStatus())) {
            return Result.error(400, "账单状态异常，无需重复支付");
        }

        // 模拟支付成功逻辑（实际项目会对接微信/支付宝SDK）
        bill.setStatus("PAID");
        bill.setPayTime(LocalDateTime.now());
        billService.updateById(bill);
        return Result.ok(null);
    }

}
