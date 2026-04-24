package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.PaymentBill;
import com.community.entity.PropertyFeeItem;
import com.community.entity.SysUser;
import com.community.service.PaymentBillService;
import com.community.service.PropertyFeeItemService;
import com.community.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/bills")
@RequiredArgsConstructor
public class AdminBillController {
    private final PaymentBillService billService;
    private final PropertyFeeItemService feeItemService;
    private final SysUserService userService;

    // 1. 账单列表查询（支持按项目、状态、业主筛选）
    @GetMapping("/page")
    public Result<Page<PaymentBill>> page(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) Long feeItemId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String ownerKeyword) {
        LambdaQueryWrapper<PaymentBill> qw = new LambdaQueryWrapper<>();
        if (feeItemId != null) qw.eq(PaymentBill::getFeeItemId, feeItemId);
        if (status != null && !status.isBlank()) qw.eq(PaymentBill::getStatus, status);

        // 关联业主姓名模糊查询（需在 Service 层或 Mapper XML 中实现，此处简化为前端传 ownerId）
        // 实际项目建议用 @Select 注解或 XML 联表查询，这里先保留基础过滤
        qw.orderByDesc(PaymentBill::getCreateTime);
        return Result.ok(billService.page(new Page<>(current, size), qw));
    }

    // 2. 批量生成账单
    @PostMapping("/generate")
    public Result<Void> generateBills(@RequestParam Long feeItemId) {
        PropertyFeeItem item = feeItemService.getById(feeItemId);
        if (item == null) return Result.error(404, "收费项目不存在");
        if (item.getStatus() == 0) return Result.error(400, "该项目已停用，无法生成账单");

        // 查询所有正常状态的业主
        List<SysUser> owners = userService.list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "OWNER")
                .eq(SysUser::getStatus, 1));

        if (owners.isEmpty()) return Result.ok(null);

        // 👇 核心修复：改为一次性计算并赋值，满足 lambda 的 effectively final 要求
        int addMonths = 1;
        if ("QUARTER".equals(item.getCycle())) addMonths = 3;
        else if ("YEAR".equals(item.getCycle())) addMonths = 12;
        final LocalDate dueDate = LocalDate.now().plusMonths(addMonths);

        // 过滤已存在本期账单的业主（防重复）
        List<Long> existingOwnerIds = billService.list(new LambdaQueryWrapper<PaymentBill>()
                        .eq(PaymentBill::getFeeItemId, feeItemId)
                        .ge(PaymentBill::getDueDate, LocalDate.now()))
                .stream().map(PaymentBill::getOwnerId).collect(Collectors.toList());

        List<PaymentBill> newBills = owners.stream()
                .filter(owner -> !existingOwnerIds.contains(owner.getId()))
                .map(owner -> {
                    PaymentBill bill = new PaymentBill();
                    bill.setOwnerId(owner.getId());
                    bill.setFeeItemId(feeItemId);
                    bill.setAmount(item.getAmount());
                    bill.setStatus("PENDING");
                    bill.setDueDate(dueDate); // 👈 现在 dueDate 是 final 的，Lambda 可以安全引用
                    bill.setCreateTime(LocalDateTime.now());
                    return bill;
                }).collect(Collectors.toList());

        if (!newBills.isEmpty()) {
            billService.saveBatch(newBills);
        }
        return Result.ok(null);
    }


    // 3. 手动修改账单状态（如线下收款后标记已缴）
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        PaymentBill bill = new PaymentBill();
        bill.setId(id);
        bill.setStatus(status);
        if ("PAID".equals(status)) bill.setPayTime(LocalDateTime.now());
        billService.updateById(bill);
        return Result.ok(null);
    }
}
