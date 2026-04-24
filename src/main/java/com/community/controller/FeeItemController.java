package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.PropertyFeeItem;
import com.community.service.PropertyFeeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/fee-items")
@RequiredArgsConstructor
public class FeeItemController {
    private final PropertyFeeItemService feeItemService;

    @GetMapping("/page")
    public Result<Page<PropertyFeeItem>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<PropertyFeeItem> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(PropertyFeeItem::getItemName, keyword);
        }
        qw.orderByDesc(PropertyFeeItem::getCreateTime);
        return Result.ok(feeItemService.page(new Page<>(current, size), qw));
    }

    @PostMapping
    public Result<Void> add(@RequestBody PropertyFeeItem item) {
        item.setStatus(1);
        feeItemService.save(item);
        return Result.ok(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PropertyFeeItem item) {
        item.setId(id);
        feeItemService.updateById(item);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        feeItemService.removeById(id);
        return Result.ok(null);
    }
}
