package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Facility;
import com.community.entity.FacilityBooking;
import com.community.service.FacilityBookingService;
import com.community.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/facility-booking")
@RequiredArgsConstructor
public class FacilityBookingController {

    private final FacilityBookingService bookingService;
    private final FacilityService facilityService;

    // ====== 业主端 ======

    /**
     * 提交借用申请
     * 前置校验：设施必须为 AVAILABLE 状态
     */
    @PostMapping
    public Result<Void> submit(@RequestBody FacilityBooking booking,
                               @RequestAttribute("userId") Long userId) {
        // 校验设施是否存在
        Facility facility = facilityService.getById(booking.getFacilityId());
        if (facility == null) return Result.error(404, "设施不存在");
        if (!"AVAILABLE".equals(facility.getStatus())) {
            return Result.error(400, "该设施当前不可借用（状态：" + facility.getStatus() + "）");
        }

        booking.setOwnerId(userId);
        booking.setStatus("PENDING");
        booking.setCreateTime(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        bookingService.save(booking);
        return Result.ok(null);
    }

    /**
     * 业主查看自己的借用记录
     */
    @GetMapping("/mine")
    public Result<Page<FacilityBooking>> mine(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String status,
                                               @RequestAttribute("userId") Long userId) {
        LambdaQueryWrapper<FacilityBooking> qw = new LambdaQueryWrapper<>();
        qw.eq(FacilityBooking::getOwnerId, userId);
        if (status != null && !status.isBlank()) qw.eq(FacilityBooking::getStatus, status);
        qw.orderByDesc(FacilityBooking::getCreateTime);

        Page<FacilityBooking> page = new Page<>(current, size);
        return Result.ok(bookingService.selectBookingPage(page, qw));
    }

    /**
     * 业主归还设施（将状态从 APPROVED 改为 RETURNED，同时释放设施）
     */
    @PutMapping("/{id}/return")
    public Result<Void> returnFacility(@PathVariable Long id,
                                        @RequestAttribute("userId") Long userId) {
        FacilityBooking booking = bookingService.getById(id);
        if (booking == null) return Result.error(404, "借用记录不存在");
        if (!booking.getOwnerId().equals(userId)) return Result.error(403, "无权操作此记录");
        if (!"APPROVED".equals(booking.getStatus())) return Result.error(400, "当前状态不允许归还");

        // 更新借用记录
        booking.setStatus("RETURNED");
        booking.setReturnTime(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        bookingService.updateById(booking);

        // 释放设施，恢复为可借用
        Facility facility = new Facility();
        facility.setId(booking.getFacilityId());
        facility.setStatus("AVAILABLE");
        facility.setUpdateTime(LocalDateTime.now());
        facilityService.updateById(facility);

        return Result.ok(null);
    }

    // ====== 管理端 ======

    /**
     * 管理员查看所有借用申请（分页 + 筛选）
     */
    @GetMapping("/page")
    public Result<Page<FacilityBooking>> page(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<FacilityBooking> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) qw.eq(FacilityBooking::getStatus, status);
        // 按设施名称关键字筛选（子查询）
        if (keyword != null && !keyword.isBlank()) {
            qw.apply("facility_id IN (SELECT id FROM facility WHERE name LIKE {0})", "%" + keyword + "%");
        }
        qw.orderByDesc(FacilityBooking::getCreateTime);

        Page<FacilityBooking> page = new Page<>(current, size);
        return Result.ok(bookingService.selectBookingPage(page, qw));
    }

    /**
     * 管理员审批申请（通过 / 驳回）
     * 通过时：设施状态改为 BOOKED
     * 驳回时：填写驳回理由
     */
    @PutMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id,
                               @RequestBody FacilityBooking req,
                               @RequestAttribute("userId") Long adminId) {
        FacilityBooking existing = bookingService.getById(id);
        if (existing == null) return Result.error(404, "借用记录不存在");
        if (!"PENDING".equals(existing.getStatus())) return Result.error(400, "该申请已被处理");

        existing.setStatus(req.getStatus());          // APPROVED 或 REJECTED
        existing.setHandlerId(adminId);
        existing.setReplyContent(req.getReplyContent());
        existing.setUpdateTime(LocalDateTime.now());
        bookingService.updateById(existing);

        // 审批通过：锁定设施
        if ("APPROVED".equals(req.getStatus())) {
            Facility facility = new Facility();
            facility.setId(existing.getFacilityId());
            facility.setStatus("BOOKED");
            facility.setUpdateTime(LocalDateTime.now());
            facilityService.updateById(facility);
        }

        return Result.ok(null);
    }
}
