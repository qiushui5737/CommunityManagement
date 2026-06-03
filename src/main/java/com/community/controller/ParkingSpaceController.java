package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.ParkingSpace;
import com.community.service.ParkingSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingSpaceController {
    private final ParkingSpaceService parkingService;

    // 1. 可视化数据接口（业主选购用）
    @GetMapping("/visual")
    public Result<List<Map<String, Object>>> getVisualData() {
        List<ParkingSpace> spaces = parkingService.list(new LambdaQueryWrapper<ParkingSpace>()
                .orderByAsc(ParkingSpace::getRowNo).orderByAsc(ParkingSpace::getColNo));

        List<Map<String, Object>> data = spaces.stream().map(s -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("spaceNo", s.getSpaceNo());
            map.put("status", s.getStatus());
            map.put("price", s.getPrice());
            map.put("type", s.getType());
            map.put("value", new Object[]{s.getColNo(), s.getRowNo(), s.getId(), s.getStatus()});
            return map;
        }).toList();
        return Result.ok(data);
    }

    // 2. 业主购买车位
    @PostMapping("/purchase/{id}")
    @Transactional
    public Result<Void> purchase(@PathVariable Long id, @RequestAttribute("userId") Long ownerId) {
        ParkingSpace space = parkingService.getById(id);
        if (space == null) return Result.error(404, "车位不存在");
        if (!"FREE".equals(space.getStatus())) return Result.error(400, "车位已被购买");

        space.setStatus("SOLD");
        space.setOwnerId(ownerId);
        parkingService.updateById(space);
        return Result.ok(null);
    }

    // 3. 业主查看已购车位
    @GetMapping("/my")
    public Result<List<ParkingSpace>> mySpaces(@RequestAttribute("userId") Long ownerId) {
        return Result.ok(parkingService.list(new LambdaQueryWrapper<ParkingSpace>()
                .eq(ParkingSpace::getOwnerId, ownerId)
                .orderByDesc(ParkingSpace::getCreateTime)));
    }

    // 4. 管理端分页 (沿用标准模式，此处省略)
}
