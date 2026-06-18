package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Announcement;
import com.community.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("NOTICE", "ACTIVITY", "MAINTENANCE", "OTHER");

    // ======================== 1. 分页查询公告 ========================
    // 业主：只能查看已发布的公告；管理员：可查看全部公告
    @GetMapping("/page")
    public Result<Page<Announcement>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String keyword,
                                           @RequestAttribute("role") String role) {
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();

        // 业主只能看到已发布的公告
        if ("OWNER".equals(role)) {
            qw.eq(Announcement::getStatus, "PUBLISHED");
        }

        // 按类型筛选
        if (type != null && !type.isBlank()) {
            qw.eq(Announcement::getType, type);
        }

        // 关键词搜索（标题/内容模糊匹配）
        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(Announcement::getTitle, keyword)
                         .or()
                         .like(Announcement::getContent, keyword));
        }

        // 置顶优先，再按创建时间倒序
        qw.orderByDesc(Announcement::getIsTop)
          .orderByDesc(Announcement::getCreateTime);

        Page<Announcement> page = new Page<>(current, size);
        return Result.ok(announcementService.selectAnnouncementPageWithPublisher(page, qw));
    }

    // ======================== 2. 查看公告详情 ========================
    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id, @RequestAttribute("role") String role) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error(404, "公告不存在");
        }
        // 业主不能查看草稿和已撤回的公告
        if ("OWNER".equals(role) && !"PUBLISHED".equals(announcement.getStatus())) {
            return Result.error(403, "无权查看该公告");
        }
        return Result.ok(announcement);
    }

    // ======================== 3. 管理员发布公告 ========================
    @PostMapping
    public Result<Void> publish(@RequestBody Announcement announcement,
                                @RequestAttribute("userId") Long userId,
                                @RequestAttribute("role") String role) {
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "仅管理员可发布公告");
        }
        if (announcement.getTitle() == null || announcement.getTitle().isBlank()) {
            return Result.error(400, "公告标题不能为空");
        }
        if (announcement.getContent() == null || announcement.getContent().isBlank()) {
            return Result.error(400, "公告内容不能为空");
        }
        if (announcement.getType() != null && !ALLOWED_TYPES.contains(announcement.getType())) {
            return Result.error(400, "公告类型不合法");
        }

        announcement.setPublisherId(userId);
        announcement.setStatus("PUBLISHED");
        announcement.setIsTop(announcement.getIsTop() != null ? announcement.getIsTop() : 0);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcementService.save(announcement);
        return Result.ok(null);
    }

    // ======================== 4. 管理员编辑公告 ========================
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody Announcement announcement,
                               @RequestAttribute("role") String role) {
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "仅管理员可编辑公告");
        }
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            return Result.error(404, "公告不存在");
        }

        if (announcement.getTitle() != null) existing.setTitle(announcement.getTitle());
        if (announcement.getContent() != null) existing.setContent(announcement.getContent());
        if (announcement.getType() != null) {
            if (!ALLOWED_TYPES.contains(announcement.getType())) {
                return Result.error(400, "公告类型不合法");
            }
            existing.setType(announcement.getType());
        }
        if (announcement.getIsTop() != null) existing.setIsTop(announcement.getIsTop());
        existing.setUpdateTime(LocalDateTime.now());

        announcementService.updateById(existing);
        return Result.ok(null);
    }

    // ======================== 5. 管理员撤回公告 ========================
    @PutMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id, @RequestAttribute("role") String role) {
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "仅管理员可撤回公告");
        }
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            return Result.error(404, "公告不存在");
        }
        if (!"PUBLISHED".equals(existing.getStatus())) {
            return Result.error(400, "只能撤回已发布的公告");
        }
        existing.setStatus("WITHDRAWN");
        existing.setUpdateTime(LocalDateTime.now());
        announcementService.updateById(existing);
        return Result.ok(null);
    }

    // ======================== 6. 管理员删除公告 ========================
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestAttribute("role") String role) {
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "仅管理员可删除公告");
        }
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            return Result.error(404, "公告不存在");
        }
        announcementService.removeById(id);
        return Result.ok(null);
    }
}
