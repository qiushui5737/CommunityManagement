package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.CommunityUnit;
import com.community.service.CommunityUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class CommunityUnitController {
    private final CommunityUnitService unitService;

    @GetMapping("/page")
    public Result<Page<CommunityUnit>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) Long buildingId,
                                            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<CommunityUnit> qw = new LambdaQueryWrapper<>();
        if (buildingId != null) qw.eq(CommunityUnit::getBuildingId, buildingId);
        if (keyword != null && !keyword.isBlank()) qw.like(CommunityUnit::getUnitNo, keyword);
        qw.orderByDesc(CommunityUnit::getCreateTime);
        return Result.ok(unitService.page(new Page<>(current, size), qw));
    }

    @PostMapping
    public Result<Void> add(@RequestBody CommunityUnit unit) {
        unitService.save(unit);
        return Result.ok(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CommunityUnit unit) {
        unit.setId(id);
        unitService.updateById(unit);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        unitService.removeById(id);
        return Result.ok(null);
    }

    // 级联数据源：根据楼栋ID获取所有单元
    @GetMapping("/by-building")
    public Result<List<CommunityUnit>> getByBuilding(@RequestParam Long buildingId) {
        return Result.ok(unitService.list(new LambdaQueryWrapper<CommunityUnit>().eq(CommunityUnit::getBuildingId, buildingId)));
    }
}
