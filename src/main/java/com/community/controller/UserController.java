package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.*;
import com.community.mapper.*;
import com.community.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final SysUserService userService;
    private final SysUserMapper userMapper;
    private final CommunityHouseMapper houseMapper;
    private final CommunityBuildingMapper buildingMapper;
    private final CommunityUnitMapper communityUnitMapper;
    private final PaymentBillMapper paymentBillMapper;
    private final RepairRequestMapper repairRequestMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final AnnouncementMapper announcementMapper;
    private final FeedbackMapper feedbackMapper;
    private final FacilityBookingMapper facilityBookingMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 1. 分页查询（联表）
    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword);
        }
        qw.orderByDesc(SysUser::getCreateTime);
        return Result.ok(userMapper.selectUserPageWithHouse(new Page<>(current, size), qw));
    }

    // 2. 新增
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        user.setPassword(encoder.encode("123456"));
        user.setStatus(1);
        userService.save(user);
        bindHouse(user.getId(), user.getHouseId());
        return Result.ok(null);
    }

    // 3. 编辑
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(userService.getById(id).getPassword());
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        bindHouse(id, user.getHouseId());
        return Result.ok(null);
    }

    // 4. 删除 & 状态切换（保持原有逻辑）
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) { userService.removeById(id); return Result.ok(null); }
    @PutMapping("/status/{id}") public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser u = new SysUser(); u.setId(id); u.setStatus(status); userService.updateById(u); return Result.ok(null);
    }

    // 5. 房屋绑定逻辑
    private void bindHouse(Long userId, Long houseId) {
        if (houseId == null) return;
        houseMapper.update(null, new UpdateWrapper<CommunityHouse>().eq("owner_id", userId).set("owner_id", null));
        CommunityHouse h = new CommunityHouse();
        h.setId(houseId);
        h.setOwnerId(userId);
        houseMapper.updateById(h);
    }

    // ========== 业主个人中心 ==========
    @GetMapping("/owner/profile")
    public Result<Map<String, Object>> ownerProfile(@RequestAttribute("userId") Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        user.setPassword(null);

        // 绑定房屋信息（支持多套房产）
        List<CommunityHouse> houses = houseMapper.selectList(
                new LambdaQueryWrapper<CommunityHouse>().eq(CommunityHouse::getOwnerId, userId));

        List<Map<String, Object>> houseList = new ArrayList<>();
        String buildingName = "";
        String unitName = "";
        String roomNo = "";

        for (CommunityHouse house : houses) {
            Map<String, Object> hMap = new HashMap<>();
            hMap.put("houseId", house.getId());
            hMap.put("roomNo", house.getRoomNo() != null ? house.getRoomNo() : "");
            hMap.put("area", house.getArea());
            hMap.put("status", house.getStatus());

            String bName = "";
            String uName = "";
            CommunityUnit unit = null;
            try { unit = communityUnitMapper.selectById(house.getUnitId()); } catch (Exception ignored) {}
            if (unit != null) {
                uName = unit.getUnitNo() != null ? unit.getUnitNo() : "";
                CommunityBuilding building = buildingMapper.selectById(unit.getBuildingId());
                if (building != null) {
                    bName = (building.getBuildingNo() != null ? building.getBuildingNo() : "") + " " + (building.getName() != null ? building.getName() : "");
                }
            }
            hMap.put("buildingName", bName.trim());
            hMap.put("unitName", uName);
            houseList.add(hMap);

            // 保留第一套房的兼容字段
            if (houseList.size() == 1) {
                buildingName = bName.trim();
                unitName = uName;
                roomNo = house.getRoomNo() != null ? house.getRoomNo() : "";
            }
        }

        // 统计
        long pendingBills = paymentBillMapper.selectCount(
                new LambdaQueryWrapper<PaymentBill>().eq(PaymentBill::getOwnerId, userId).eq(PaymentBill::getStatus, "PENDING"));
        long activeRepairs = repairRequestMapper.selectCount(
                new LambdaQueryWrapper<RepairRequest>().eq(RepairRequest::getOwnerId, userId).in(RepairRequest::getStatus, "PENDING", "PROCESSING"));
        long parkingCount = parkingSpaceMapper.selectCount(
                new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getOwnerId, userId));

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("houses", houseList);
        result.put("houseCount", houseList.size());
        result.put("buildingName", buildingName);
        result.put("unitName", unitName);
        result.put("roomNo", roomNo);
        result.put("pendingBills", pendingBills);
        result.put("activeRepairs", activeRepairs);
        result.put("parkingCount", parkingCount);
        return Result.ok(result);
    }

    // 修改密码
    @PutMapping("/owner/password")
    public Result<Void> changePassword(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> body) {
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");
        if (oldPwd == null || newPwd == null || newPwd.length() < 6) return Result.error(400, "新密码不能少于6位");
        SysUser user = userService.getById(userId);
        if (!encoder.matches(oldPwd, user.getPassword())) return Result.error(400, "原密码错误");
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(encoder.encode(newPwd));
        userService.updateById(update);
        return Result.ok(null);
    }

    // 修改个人信息
    @PutMapping("/owner/info")
    public Result<Void> updateInfo(@RequestAttribute("userId") Long userId, @RequestBody SysUser user) {
        SysUser update = new SysUser();
        update.setId(userId);
        update.setRealName(user.getRealName());
        update.setPhone(user.getPhone());
        update.setAvatarUrl(user.getAvatarUrl());
        update.setEmail(user.getEmail());
        update.setGender(user.getGender());
        update.setBirthday(user.getBirthday());
        update.setIdCard(user.getIdCard());
        update.setEmergencyContact(user.getEmergencyContact());
        update.setEmergencyPhone(user.getEmergencyPhone());
        userService.updateById(update);
        return Result.ok(null);
    }

    // ========== 业主消息通知 ==========
    @GetMapping("/owner/notifications")
    public Result<Map<String, Object>> ownerNotifications(@RequestAttribute("userId") Long userId) {
        List<Map<String, Object>> items = new ArrayList<>();

        // 1. 待缴账单
        List<PaymentBill> pendingBills = paymentBillMapper.selectList(
                new LambdaQueryWrapper<PaymentBill>()
                        .eq(PaymentBill::getOwnerId, userId)
                        .in(PaymentBill::getStatus, "PENDING", "OVERDUE")
                        .orderByDesc(PaymentBill::getCreateTime));
        for (PaymentBill b : pendingBills) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", "BILL");
            m.put("title", b.getStatus().equals("OVERDUE") ? "账单逾期提醒" : "待缴账单通知");
            m.put("message", "您有一笔 ¥" + b.getAmount() + " 的账单" + (b.getStatus().equals("OVERDUE") ? "已逾期，请尽快缴纳" : "待缴纳"));
            m.put("time", b.getCreateTime());
            m.put("link", "/owner/bills");
            m.put("level", b.getStatus().equals("OVERDUE") ? "danger" : "warning");
            items.add(m);
        }

        // 2. 报修状态变更（已完成 / 已驳回）
        List<RepairRequest> doneRepairs = repairRequestMapper.selectList(
                new LambdaQueryWrapper<RepairRequest>()
                        .eq(RepairRequest::getOwnerId, userId)
                        .in(RepairRequest::getStatus, "COMPLETED", "REJECTED")
                        .orderByDesc(RepairRequest::getUpdateTime));
        for (RepairRequest r : doneRepairs) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", "REPAIR");
            boolean completed = "COMPLETED".equals(r.getStatus());
            m.put("title", completed ? "报修已完成" : "报修已驳回");
            m.put("message", "[" + r.getTitle() + "] " + (completed ? "已处理完毕" : "未通过审批")
                    + (r.getReplyContent() != null && !r.getReplyContent().isBlank() ? "：" + r.getReplyContent() : ""));
            m.put("time", r.getUpdateTime() != null ? r.getUpdateTime() : r.getCreateTime());
            m.put("link", "/owner/repairs");
            m.put("level", completed ? "success" : "danger");
            items.add(m);
        }

        // 3. 近7天新公告
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Announcement> recentAnnouncements = announcementMapper.selectList(
                new LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getStatus, "PUBLISHED")
                        .ge(Announcement::getCreateTime, sevenDaysAgo)
                        .orderByDesc(Announcement::getIsTop)
                        .orderByDesc(Announcement::getCreateTime));
        for (Announcement a : recentAnnouncements) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", "ANNOUNCEMENT");
            m.put("title", "新公告");
            m.put("message", a.getTitle());
            m.put("time", a.getCreateTime());
            m.put("link", "/owner/announcements");
            m.put("level", "info");
            items.add(m);
        }

        // 4. 反馈已回复
        List<Feedback> repliedFeedbacks = feedbackMapper.selectList(
                new LambdaQueryWrapper<Feedback>()
                        .eq(Feedback::getOwnerId, userId)
                        .eq(Feedback::getStatus, "REPLIED")
                        .orderByDesc(Feedback::getUpdateTime));
        for (Feedback f : repliedFeedbacks) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", "FEEDBACK");
            m.put("title", "反馈已回复");
            m.put("message", "您的反馈 [" + f.getTitle() + "] 已收到回复，请查看");
            m.put("time", f.getUpdateTime() != null ? f.getUpdateTime() : f.getCreateTime());
            m.put("link", "/owner/feedback");
            m.put("level", "success");
            items.add(m);
        }

        // 5. 设施借用审批结果
        List<FacilityBooking> bookingResults = facilityBookingMapper.selectList(
                new LambdaQueryWrapper<FacilityBooking>()
                        .eq(FacilityBooking::getOwnerId, userId)
                        .in(FacilityBooking::getStatus, "APPROVED", "REJECTED")
                        .orderByDesc(FacilityBooking::getUpdateTime));
        for (FacilityBooking fb : bookingResults) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", "BOOKING");
            boolean approved = "APPROVED".equals(fb.getStatus());
            m.put("title", approved ? "借用申请已通过" : "借用申请已驳回");
            m.put("message", approved ? "您的设施借用申请已审批通过" : "您的设施借用申请未通过"
                    + (fb.getReplyContent() != null && !fb.getReplyContent().isBlank() ? "：" + fb.getReplyContent() : ""));
            m.put("time", fb.getUpdateTime() != null ? fb.getUpdateTime() : fb.getCreateTime());
            m.put("link", "/owner/facilities");
            m.put("level", approved ? "success" : "danger");
            items.add(m);
        }

        // 按时间倒序排列
        items.sort((a, b) -> {
            LocalDateTime ta = (LocalDateTime) a.get("time");
            LocalDateTime tb = (LocalDateTime) b.get("time");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        // 限制最多 20 条
        List<Map<String, Object>> limited = items.size() > 20 ? items.subList(0, 20) : items;

        Map<String, Object> result = new HashMap<>();
        result.put("notifications", limited);
        result.put("totalCount", limited.size());
        return Result.ok(result);
    }

    // 6. 级联下拉数据源

    @GetMapping("/buildings")
    public Result<List<Map<String, Object>>> getBuildings() {
        List<Map<String, Object>> list = buildingMapper.selectList(null).stream()
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", b.getId());
                    map.put("name", b.getBuildingNo() + " - " + b.getName());
                    return map;
                }).collect(Collectors.toList());
        return Result.ok(list);
    }

    @GetMapping("/houses")
    public Result<List<Map<String, Object>>> getHouses(@RequestParam Long buildingId) {
        List<Map<String, Object>> list = houseMapper.selectList(
                new LambdaQueryWrapper<CommunityHouse>()
                        .inSql(CommunityHouse::getUnitId, "SELECT id FROM community_unit WHERE building_id = " + buildingId)
        ).stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", h.getId());
            map.put("roomNo", h.getRoomNo());
            return map;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }
}
