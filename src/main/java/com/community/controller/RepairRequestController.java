package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.RepairRequest;
import com.community.service.RepairRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class RepairRequestController {
    private final RepairRequestService repairService;
    private static final List<String> ALLOWED_CATEGORIES = Arrays.asList("水电", "门窗", "电梯", "公共设施", "其他");

    // 1. 分页查询（角色隔离：业主只能看自己的，管理员可看全部）
    @GetMapping("/page")
    public Result<Page<RepairRequest>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String status,
                                            @RequestAttribute("userId") Long userId,
                                            @RequestAttribute("role") String role) {
        LambdaQueryWrapper<RepairRequest> qw = new LambdaQueryWrapper<>();
        if ("OWNER".equals(role)) qw.eq(RepairRequest::getOwnerId, userId);
        if (status != null && !status.isBlank()) qw.eq(RepairRequest::getStatus, status);
        qw.orderByDesc(RepairRequest::getCreateTime);

        Page<RepairRequest> page = new Page<>(current, size);
        // 👇 关键修改：使用联表查询方法

        return Result.ok(repairService.selectRepairPageWithOwner(page, qw));
    }

    // 2. 业主提交报修
    @PostMapping
    public Result<Void> submit(@RequestBody RepairRequest req, @RequestAttribute("userId") Long userId) {
        if (!ALLOWED_CATEGORIES.contains(req.getCategory())) {
            return Result.error(400, "报修类型不合法");
        }
        req.setOwnerId(userId);
        req.setStatus("PENDING");
        req.setCreateTime(LocalDateTime.now());
        repairService.save(req);
        return Result.ok(null);
    }

    // 3. 管理员处理工单（分配/回复/改状态）
    @PutMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id, @RequestBody RepairRequest req) {
        RepairRequest existing = repairService.getById(id);
        if (existing == null) return Result.error(404, "工单不存在");

        existing.setStatus(req.getStatus());
        if (req.getHandlerId() != null) existing.setHandlerId(req.getHandlerId());
        if (req.getReplyContent() != null) existing.setReplyContent(req.getReplyContent());
        existing.setUpdateTime(LocalDateTime.now());

        repairService.updateById(existing);
        return Result.ok(null);
    }
}
