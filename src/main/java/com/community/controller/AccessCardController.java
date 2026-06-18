package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.AccessCard;
import com.community.service.AccessCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/access-card")
@RequiredArgsConstructor
public class AccessCardController {

    private final AccessCardService cardService;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("OWNER", "FAMILY", "VISITOR", "TEMPORARY");
    private static final Random RANDOM = new Random();

    // ============ 分页查询（业主只看自己，管理员看全部）============
    @GetMapping("/page")
    public Result<Page<AccessCard>> page(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String cardType,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String keyword,
                                         @RequestAttribute("userId") Long userId,
                                         @RequestAttribute("role") String role) {
        Long ownerId = "OWNER".equals(role) ? userId : null;
        Page<AccessCard> page = new Page<>(current, size);
        return Result.ok(cardService.selectCardPage(page, ownerId, cardType, status, keyword));
    }

    // ============ 业主查看我的门禁卡 ============
    @GetMapping("/my")
    public Result<List<AccessCard>> myCards(@RequestAttribute("userId") Long userId) {
        return Result.ok(cardService.list(new LambdaQueryWrapper<AccessCard>()
                .eq(AccessCard::getOwnerId, userId)
                .orderByDesc(AccessCard::getCreateTime)));
    }

    // ============ 管理员发行卡片 ============
    @PostMapping
    public Result<Void> issue(@RequestBody AccessCard card) {
        if (card.getCardType() != null && !ALLOWED_TYPES.contains(card.getCardType())) {
            return Result.error(400, "卡片类型不合法");
        }
        // 自动生成卡片编号
        if (card.getCardNo() == null || card.getCardNo().isBlank()) {
            card.setCardNo(generateCardNo());
        } else {
            // 检查编号是否重复
            long cnt = cardService.count(new LambdaQueryWrapper<AccessCard>()
                    .eq(AccessCard::getCardNo, card.getCardNo()));
            if (cnt > 0) return Result.error(400, "卡片编号已存在");
        }
        card.setStatus("ACTIVE");
        card.setCreateTime(LocalDateTime.now());
        cardService.save(card);
        return Result.ok(null);
    }

    // ============ 管理员编辑卡片（权限设置）============
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AccessCard req) {
        AccessCard card = cardService.getById(id);
        if (card == null) return Result.error(404, "卡片不存在");

        if (req.getCardType() != null && ALLOWED_TYPES.contains(req.getCardType())) {
            card.setCardType(req.getCardType());
        }
        if (req.getBuildingIds() != null) card.setBuildingIds(req.getBuildingIds());
        if (req.getValidFrom() != null) card.setValidFrom(req.getValidFrom());
        if (req.getValidTo() != null) card.setValidTo(req.getValidTo());
        if (req.getRemark() != null) card.setRemark(req.getRemark());
        if (req.getOwnerId() != null) card.setOwnerId(req.getOwnerId());
        card.setUpdateTime(LocalDateTime.now());
        cardService.updateById(card);
        return Result.ok(null);
    }

    // ============ 挂失（冻结卡片）============
    @PutMapping("/{id}/suspend")
    public Result<Void> suspend(@PathVariable Long id) {
        AccessCard card = cardService.getById(id);
        if (card == null) return Result.error(404, "卡片不存在");
        if (!"ACTIVE".equals(card.getStatus())) return Result.error(400, "只有正常状态的卡片可以挂失");
        card.setStatus("SUSPENDED");
        card.setUpdateTime(LocalDateTime.now());
        cardService.updateById(card);
        return Result.ok(null);
    }

    // ============ 解除挂失 ============
    @PutMapping("/{id}/resume")
    public Result<Void> resume(@PathVariable Long id) {
        AccessCard card = cardService.getById(id);
        if (card == null) return Result.error(404, "卡片不存在");
        if (!"SUSPENDED".equals(card.getStatus())) return Result.error(400, "只有挂失状态的卡片可以恢复");
        card.setStatus("ACTIVE");
        card.setUpdateTime(LocalDateTime.now());
        cardService.updateById(card);
        return Result.ok(null);
    }

    // ============ 注销卡片 ============
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        AccessCard card = cardService.getById(id);
        if (card == null) return Result.error(404, "卡片不存在");
        if ("CANCELLED".equals(card.getStatus())) return Result.error(400, "卡片已注销");
        card.setStatus("CANCELLED");
        card.setUpdateTime(LocalDateTime.now());
        cardService.updateById(card);
        return Result.ok(null);
    }

    // ============ 删除卡片（仅注销状态可删除）============
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AccessCard card = cardService.getById(id);
        if (card == null) return Result.error(404, "卡片不存在");
        if (!"CANCELLED".equals(card.getStatus())) return Result.error(400, "只能删除已注销的卡片");
        cardService.removeById(id);
        return Result.ok(null);
    }

    // ============ 统计 ============
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> m = new HashMap<>();
        m.put("total", cardService.count());
        m.put("active", cardService.count(new LambdaQueryWrapper<AccessCard>().eq(AccessCard::getStatus, "ACTIVE")));
        m.put("suspended", cardService.count(new LambdaQueryWrapper<AccessCard>().eq(AccessCard::getStatus, "SUSPENDED")));
        m.put("cancelled", cardService.count(new LambdaQueryWrapper<AccessCard>().eq(AccessCard::getStatus, "CANCELLED")));
        return Result.ok(m);
    }

    // 自动生成卡片编号：AC + 年月日 + 4位随机数
    private String generateCardNo() {
        String date = LocalDate.now().toString().replace("-", "");
        String rand = String.format("%04d", RANDOM.nextInt(10000));
        return "AC" + date + rand;
    }
}
