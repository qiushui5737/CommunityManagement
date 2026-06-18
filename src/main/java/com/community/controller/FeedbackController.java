package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Feedback;
import com.community.entity.FeedbackReply;
import com.community.service.FeedbackReplyService;
import com.community.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final FeedbackReplyService replyService;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("SUGGESTION", "COMPLAINT", "INQUIRY");

    /** 分页查询（业主只看自己，管理员看全部） */
    @GetMapping("/page")
    public Result<Page<Feedback>> page(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(required = false) String status,
                                       @RequestAttribute("userId") Long userId,
                                       @RequestAttribute("role") String role) {
        Long ownerId = "OWNER".equals(role) ? userId : null;
        Page<Feedback> page = new Page<>(current, size);
        return Result.ok(feedbackService.selectFeedbackPage(page, ownerId, type, status));
    }

    /** 获取单条反馈详情 */
    @GetMapping("/{id}")
    public Result<Feedback> detail(@PathVariable Long id,
                                   @RequestAttribute("userId") Long userId,
                                   @RequestAttribute("role") String role) {
        Feedback fb = feedbackService.getById(id);
        if (fb == null) return Result.error(404, "反馈不存在");
        if ("OWNER".equals(role) && !fb.getOwnerId().equals(userId)) return Result.error(403, "无权查看");
        return Result.ok(fb);
    }

    /** 获取回复列表 */
    @GetMapping("/{id}/replies")
    public Result<List<FeedbackReply>> replies(@PathVariable Long id) {
        return Result.ok(replyService.selectRepliesByFeedbackId(id));
    }

    /** 业主提交反馈 */
    @PostMapping
    public Result<Void> submit(@RequestBody Feedback fb, @RequestAttribute("userId") Long userId) {
        if (!ALLOWED_TYPES.contains(fb.getType())) return Result.error(400, "反馈类型不合法");
        fb.setOwnerId(userId);
        fb.setStatus("PENDING");
        fb.setCreateTime(LocalDateTime.now());
        feedbackService.save(fb);
        return Result.ok(null);
    }

    /** 物业回复反馈 */
    @PostMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id,
                              @RequestBody FeedbackReply reply,
                              @RequestAttribute("userId") Long userId,
                              @RequestAttribute("role") String role) {
        Feedback fb = feedbackService.getById(id);
        if (fb == null) return Result.error(404, "反馈不存在");

        reply.setFeedbackId(id);
        reply.setUserId(userId);
        reply.setUserRole(role);
        reply.setCreateTime(LocalDateTime.now());
        replyService.save(reply);

        // 更新反馈状态
        if ("OWNER".equals(role)) {
            fb.setStatus("PROCESSING");
        } else {
            fb.setStatus("REPLIED");
            fb.setHandlerId(userId);
        }
        fb.setUpdateTime(LocalDateTime.now());
        feedbackService.updateById(fb);
        return Result.ok(null);
    }

    /** 管理员关闭反馈 */
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        Feedback fb = feedbackService.getById(id);
        if (fb == null) return Result.error(404, "反馈不存在");
        fb.setStatus("CLOSED");
        fb.setUpdateTime(LocalDateTime.now());
        feedbackService.updateById(fb);
        return Result.ok(null);
    }
}
