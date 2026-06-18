package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.CommunityHouse;
import com.community.entity.SysUser;
import com.community.mapper.CommunityHouseMapper;
import com.community.service.CommunityHouseService;
import com.community.service.SysUserService;
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
    private final SysUserService userService;

    // 1. 分页查询
    @GetMapping("/page")
    public Result<Page<CommunityHouse>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) Long unitId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<CommunityHouse> qw = new LambdaQueryWrapper<>();
        if (unitId != null) qw.eq(CommunityHouse::getUnitId, unitId);
        if (status != null && !status.isBlank()) qw.eq(CommunityHouse::getStatus, status);
        if (keyword != null && !keyword.isBlank()) qw.like(CommunityHouse::getRoomNo, keyword);
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
        // 若设置了业主ID，校验业主是否存在
        if (house.getOwnerId() != null) {
            SysUser owner = userService.getById(house.getOwnerId());
            if (owner == null) return Result.error(400, "业主ID " + house.getOwnerId() + " 不存在");
            if (!"OWNER".equalsIgnoreCase(owner.getRole())) return Result.error(400, "用户 " + house.getOwnerId() + " 不是业主角色");
            // 自动设置状态为已入住
            house.setStatus("OCCUPIED");
        }
        houseService.updateById(house);
        return Result.ok(null);
    }

    // 3.5 办理入住
    @PostMapping("/checkin")
    public Result<Void> checkin(@RequestParam Long houseId, @RequestParam Long ownerId) {
        SysUser owner = userService.getById(ownerId);
        if (owner == null) return Result.error(400, "业主ID " + ownerId + " 不存在");
        if (!"OWNER".equalsIgnoreCase(owner.getRole())) return Result.error(400, "用户 " + ownerId + " 不是业主角色");
        CommunityHouse house = houseService.getById(houseId);
        if (house == null) return Result.error(404, "房屋不存在");
        // 解除该业主旧绑定的房屋
        CommunityHouse clear = new CommunityHouse();
        clear.setOwnerId(null);
        clear.setStatus("VACANT");
        houseService.update(clear, new LambdaQueryWrapper<CommunityHouse>().eq(CommunityHouse::getOwnerId, ownerId));
        // 绑定新房屋
        house.setOwnerId(ownerId);
        house.setStatus("OCCUPIED");
        houseService.updateById(house);
        return Result.ok(null);
    }

    // 3.6 办理退租（清空业主绑定）
    @PostMapping("/checkout")
    public Result<Void> checkout(@RequestParam Long houseId) {
        CommunityHouse house = houseService.getById(houseId);
        if (house == null) return Result.error(404, "房屋不存在");
        house.setOwnerId(null);
        house.setStatus("VACANT");
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
