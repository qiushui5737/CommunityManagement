package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.CommunityHouse;
import com.community.mapper.CommunityHouseMapper;
import com.community.service.CommunityHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class CommunityHouseController {
    private final CommunityHouseService houseService;
    private final CommunityHouseMapper houseMapper;

    // 1. 分页查询
    @GetMapping("/page")
    public Result<Page<CommunityHouse>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) Long unitId,
                                             @RequestParam(required = false) String status) {
        LambdaQueryWrapper<CommunityHouse> qw = new LambdaQueryWrapper<>();
        if (unitId != null) qw.eq(CommunityHouse::getUnitId, unitId);
        if (status != null && !status.isBlank()) qw.eq(CommunityHouse::getStatus, status);
        qw.orderByDesc(CommunityHouse::getCreateTime);
        return Result.ok(houseService.page(new Page<>(current, size), qw));
    }

    // 2. 新增
    @PostMapping
    public Result<Void> add(@RequestBody CommunityHouse house) {
        houseService.save(house);
        return Result.ok(null);
    }

    // 3. 编辑
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CommunityHouse house) {
        house.setId(id);
        houseService.updateById(house);
        return Result.ok(null);
    }

    // 4. 删除
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        houseService.removeById(id);
        return Result.ok(null);
    }

    // 5. 级联数据源：根据单元ID获取房屋列表
    @GetMapping("/by-unit")
    public Result<List<CommunityHouse>> getByUnit(@RequestParam Long unitId) {
        return Result.ok(houseService.list(new LambdaQueryWrapper<CommunityHouse>().eq(CommunityHouse::getUnitId, unitId)));
    }

    //  6. 楼栋平面图可视化专用接口
    @GetMapping("/map-data")
    public Result<List<Map<String, Object>>> getMapData(@RequestParam Long buildingId) {
        // 调用 Mapper 中的自定义联表查询，返回 ECharts 所需的扁平化结构
        List<Map<String, Object>> mapData = houseMapper.selectBuildingMapData(buildingId);
        return Result.ok(mapData);
    }
}
