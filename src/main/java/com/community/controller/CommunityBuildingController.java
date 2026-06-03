package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.CommunityBuilding;
import com.community.service.CommunityBuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class CommunityBuildingController {
    private final CommunityBuildingService buildingService;

    @GetMapping("/page")
    public Result<Page<CommunityBuilding>> page(@RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<CommunityBuilding> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(CommunityBuilding::getName, keyword).or().like(CommunityBuilding::getBuildingNo, keyword);
        }
        qw.orderByDesc(CommunityBuilding::getCreateTime);
        return Result.ok(buildingService.page(new Page<>(current, size), qw));
    }

    @PostMapping
    public Result<Void> add(@RequestBody CommunityBuilding building) {
        buildingService.save(building);
        return Result.ok(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CommunityBuilding building) {
        building.setId(id);
        buildingService.updateById(building);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.removeById(id);
        return Result.ok(null);
    }
}
