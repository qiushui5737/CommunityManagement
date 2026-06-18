package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.ParkingSpace;
import com.community.entity.CommunityBuilding;
import com.community.entity.SysUser;
import com.community.service.ParkingSpaceService;
import com.community.service.CommunityBuildingService;
import com.community.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingSpaceController {
    private final ParkingSpaceService parkingService;
    private final CommunityBuildingService buildingService;
    private final SysUserService userService;

    private static final List<String> ALLOWED_TYPES = Arrays.asList("STANDARD", "COMPACT", "LARGE", "VIP");
    private static final List<String> ALLOWED_STATUS = Arrays.asList("FREE", "LOCKED", "SOLD", "RESERVED");

    // ==================== 业主端接口 ====================

    // 0. 获取有车位数据的楼栋列表
    @GetMapping("/buildings")
    public Result<List<Map<String, Object>>> buildingList() {
        List<ParkingSpace> all = parkingService.list();
        Map<Long, List<ParkingSpace>> grouped = all.stream()
                .filter(s -> s.getBuildingId() != null)
                .collect(Collectors.groupingBy(ParkingSpace::getBuildingId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, List<ParkingSpace>> entry : grouped.entrySet()) {
            Long bid = entry.getKey();
            List<ParkingSpace> spaces = entry.getValue();
            CommunityBuilding b = buildingService.getById(bid);
            Map<String, Object> m = new HashMap<>();
            m.put("buildingId", bid);
            m.put("buildingName", b != null ? b.getName() : "");
            m.put("buildingNo", b != null ? b.getBuildingNo() : "");
            m.put("total", spaces.size());
            m.put("free", spaces.stream().filter(s -> "FREE".equals(s.getStatus())).count());
            m.put("sold", spaces.stream().filter(s -> "SOLD".equals(s.getStatus())).count());
            m.put("locked", spaces.stream().filter(s -> "LOCKED".equals(s.getStatus())).count());
            m.put("reserved", spaces.stream().filter(s -> "RESERVED".equals(s.getStatus())).count());
            result.add(m);
        }
        return Result.ok(result);
    }

    // 1. 可视化数据接口（返回全部车位，前端按状态着色）
    @GetMapping("/visual")
    public Result<List<Map<String, Object>>> getVisualData(@RequestParam(required = false) Long buildingId,
                                                           @RequestParam(required = false) String zone,
                                                           @RequestParam(required = false) String type) {
        LambdaQueryWrapper<ParkingSpace> qw = new LambdaQueryWrapper<>();
        if (buildingId != null) qw.eq(ParkingSpace::getBuildingId, buildingId);
        if (zone != null && !zone.isBlank()) qw.eq(ParkingSpace::getZone, zone);
        if (type != null && !type.isBlank()) qw.eq(ParkingSpace::getType, type);
        qw.orderByAsc(ParkingSpace::getZone)
          .orderByAsc(ParkingSpace::getRowNo)
          .orderByAsc(ParkingSpace::getColNo);

        List<ParkingSpace> spaces = parkingService.list(qw);
        List<Map<String, Object>> data = spaces.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("spaceNo", s.getSpaceNo());
            map.put("zone", s.getZone());
            map.put("rowNo", s.getRowNo());
            map.put("colNo", s.getColNo());
            map.put("status", s.getStatus());
            map.put("price", s.getPrice());
            map.put("type", s.getType());
            map.put("area", s.getArea());
            return map;
        }).toList();
        return Result.ok(data);
    }

    // 1-2. 网格可视化接口（按楼栋-行-列结构化数据，前端直接渲染地图）
    @GetMapping("/visual/grid")
    public Result<Map<String, Object>> getGridData(@RequestParam(required = false) Long buildingId) {
        LambdaQueryWrapper<ParkingSpace> qw = new LambdaQueryWrapper<>();
        if (buildingId != null) qw.eq(ParkingSpace::getBuildingId, buildingId);
        qw.orderByAsc(ParkingSpace::getBuildingId)
          .orderByAsc(ParkingSpace::getRowNo)
          .orderByAsc(ParkingSpace::getColNo);

        List<ParkingSpace> spaces = parkingService.list(qw);

        // 批量查询已售车位的业主姓名
        Set<Long> ownerIds = spaces.stream()
                .filter(s -> s.getOwnerId() != null)
                .map(ParkingSpace::getOwnerId)
                .collect(Collectors.toSet());
        Map<Long, String> ownerNameMap = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            userService.listByIds(ownerIds).forEach(u ->
                    ownerNameMap.put(u.getId(), u.getRealName() != null ? u.getRealName().trim() : "业主" + u.getId()));
        }

        // 按楼栋分组
        Map<Long, List<ParkingSpace>> buildingMap = spaces.stream()
                .filter(s -> s.getBuildingId() != null)
                .collect(Collectors.groupingBy(ParkingSpace::getBuildingId, LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> zones = new ArrayList<>();
        for (Map.Entry<Long, List<ParkingSpace>> entry : buildingMap.entrySet()) {
            Long bid = entry.getKey();
            List<ParkingSpace> buildingSpaces = entry.getValue();

            Map<String, Object> zoneData = new HashMap<>();
            zoneData.put("buildingId", bid);
            CommunityBuilding b = buildingService.getById(bid);
            zoneData.put("zone", b != null ? b.getName() : bid.toString());

            int maxRow = buildingSpaces.stream().mapToInt(s -> s.getRowNo() != null ? s.getRowNo() : 0).max().orElse(0);
            int maxCol = buildingSpaces.stream().mapToInt(s -> s.getColNo() != null ? s.getColNo() : 0).max().orElse(0);
            zoneData.put("maxRow", maxRow);
            zoneData.put("maxCol", maxCol);

            // 构建二维网格
            Map<String, Object>[][] grid = new HashMap[maxRow + 1][maxCol + 1];
            for (ParkingSpace s : buildingSpaces) {
                int r = s.getRowNo() != null ? s.getRowNo() : 0;
                int c = s.getColNo() != null ? s.getColNo() : 0;
                Map<String, Object> cell = new HashMap<>();
                cell.put("id", s.getId());
                cell.put("spaceNo", s.getSpaceNo());
                cell.put("status", s.getStatus());
                cell.put("type", s.getType());
                cell.put("area", s.getArea());
                cell.put("price", s.getPrice());
                cell.put("buildingId", s.getBuildingId());
                if (s.getOwnerId() != null) {
                    cell.put("ownerId", s.getOwnerId());
                    cell.put("ownerName", ownerNameMap.getOrDefault(s.getOwnerId(), "业主" + s.getOwnerId()));
                }
                grid[r][c] = cell;
            }

            // 转为行列表
            List<List<Map<String, Object>>> rows = new ArrayList<>();
            for (int r = 1; r <= maxRow; r++) {
                List<Map<String, Object>> row = new ArrayList<>();
                for (int c = 1; c <= maxCol; c++) {
                    row.add(grid[r][c]);
                }
                rows.add(row);
            }
            zoneData.put("rows", rows);

            // 统计
            long free = buildingSpaces.stream().filter(s -> "FREE".equals(s.getStatus())).count();
            long sold = buildingSpaces.stream().filter(s -> "SOLD".equals(s.getStatus())).count();
            long locked = buildingSpaces.stream().filter(s -> "LOCKED".equals(s.getStatus())).count();
            long reserved = buildingSpaces.stream().filter(s -> "RESERVED".equals(s.getStatus())).count();
            zoneData.put("total", buildingSpaces.size());
            zoneData.put("free", free);
            zoneData.put("sold", sold);
            zoneData.put("locked", locked);
            zoneData.put("reserved", reserved);

            zones.add(zoneData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("zones", zones);
        return Result.ok(result);
    }

    // 1-3. 车位概览统计（按楼栋分组）
    @GetMapping("/visual/summary")
    public Result<List<Map<String, Object>>> summary(@RequestParam(required = false) Long buildingId) {
        LambdaQueryWrapper<ParkingSpace> qw = new LambdaQueryWrapper<>();
        if (buildingId != null) qw.eq(ParkingSpace::getBuildingId, buildingId);
        List<ParkingSpace> all = parkingService.list(qw);
        Map<Long, List<ParkingSpace>> buildingMap = all.stream()
                .filter(s -> s.getBuildingId() != null)
                .collect(Collectors.groupingBy(ParkingSpace::getBuildingId));

        List<Map<String, Object>> result = buildingMap.entrySet().stream().map(entry -> {
            List<ParkingSpace> spaces = entry.getValue();
            Map<String, Object> m = new HashMap<>();
            m.put("buildingId", entry.getKey());
            CommunityBuilding b2 = buildingService.getById(entry.getKey());
            m.put("buildingName", b2 != null ? b2.getName() : "");
            m.put("zone", b2 != null ? b2.getName() : entry.getKey().toString());
            m.put("total", spaces.size());
            m.put("free", spaces.stream().filter(s -> "FREE".equals(s.getStatus())).count());
            m.put("sold", spaces.stream().filter(s -> "SOLD".equals(s.getStatus())).count());
            m.put("locked", spaces.stream().filter(s -> "LOCKED".equals(s.getStatus())).count());
            m.put("reserved", spaces.stream().filter(s -> "RESERVED".equals(s.getStatus())).count());
            return m;
        }).toList();
        return Result.ok(result);
    }

    // 2. 业主购买车位
    @PostMapping("/purchase/{id}")
    @Transactional
    public Result<Void> purchase(@PathVariable Long id,
                                 @RequestAttribute("userId") Long ownerId,
                                 @RequestParam(required = false) BigDecimal purchasePrice) {
        ParkingSpace space = parkingService.getById(id);
        if (space == null) return Result.error(404, "车位不存在");
        if (!"FREE".equals(space.getStatus())) return Result.error(400, "车位已被购买或锁定");

        space.setStatus("SOLD");
        space.setOwnerId(ownerId);
        space.setPurchasePrice(purchasePrice != null ? purchasePrice : space.getPrice());
        space.setPurchaseTime(LocalDateTime.now());
        space.setUpdateTime(LocalDateTime.now());
        parkingService.updateById(space);
        return Result.ok(null);
    }

    // 3. 业主查看已购车位
    @GetMapping("/my")
    public Result<List<ParkingSpace>> mySpaces(@RequestAttribute("userId") Long ownerId) {
        return Result.ok(parkingService.list(new LambdaQueryWrapper<ParkingSpace>()
                .eq(ParkingSpace::getOwnerId, ownerId)
                .orderByDesc(ParkingSpace::getPurchaseTime)));
    }

    // 4. 业主释放/退购车位（管理员可重新上架）
    @PutMapping("/release/{id}")
    @Transactional
    public Result<Void> release(@PathVariable Long id, @RequestAttribute("userId") Long userId,
                                @RequestAttribute("role") String role) {
        ParkingSpace space = parkingService.getById(id);
        if (space == null) return Result.error(404, "车位不存在");
        // 业主只能释放自己的车位，管理员可释放任意
        if ("OWNER".equals(role) && !space.getOwnerId().equals(userId)) {
            return Result.error(403, "无权操作该车位");
        }
        if (!"SOLD".equals(space.getStatus())) return Result.error(400, "只有已售车位可释放");

        space.setStatus("FREE");
        space.setOwnerId(null);
        space.setPurchasePrice(null);
        space.setPurchaseTime(null);
        space.setUpdateTime(LocalDateTime.now());
        parkingService.updateById(space);
        return Result.ok(null);
    }

    // ==================== 管理端接口 ====================

    // 5. 管理端分页查询（支持按状态、区域、类型、业主关键字筛选）
    @GetMapping("/admin/page")
    public Result<Page<ParkingSpace>> adminPage(@RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(required = false) String zone,
                                                @RequestParam(required = false) String type,
                                                @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<ParkingSpace> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) qw.eq(ParkingSpace::getStatus, status);
        if (zone != null && !zone.isBlank()) qw.eq(ParkingSpace::getZone, zone);
        if (type != null && !type.isBlank()) qw.eq(ParkingSpace::getType, type);
        qw.orderByDesc(ParkingSpace::getCreateTime);

        Page<ParkingSpace> page = new Page<>(current, size);
        return Result.ok(parkingService.selectParkingPageWithOwner(page, qw));
    }

    // 6. 管理端新增车位
    @PostMapping("/admin")
    public Result<Void> add(@RequestBody ParkingSpace space) {
        if (space.getSpaceNo() == null || space.getSpaceNo().isBlank()) {
            return Result.error(400, "车位编号不能为空");
        }
        if (space.getType() != null && !ALLOWED_TYPES.contains(space.getType())) {
            return Result.error(400, "车位类型不合法");
        }
        // 检查编号是否重复
        long existCount = parkingService.count(new LambdaQueryWrapper<ParkingSpace>()
                .eq(ParkingSpace::getSpaceNo, space.getSpaceNo()));
        if (existCount > 0) return Result.error(400, "车位编号已存在");

        space.setStatus("FREE");
        space.setCreateTime(LocalDateTime.now());
        space.setUpdateTime(LocalDateTime.now());
        parkingService.save(space);
        return Result.ok(null);
    }

    // 7. 管理端编辑车位
    @PutMapping("/admin/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ParkingSpace req) {
        ParkingSpace space = parkingService.getById(id);
        if (space == null) return Result.error(404, "车位不存在");

        // 编号修改时需检查重复
        if (req.getSpaceNo() != null && !req.getSpaceNo().equals(space.getSpaceNo())) {
            long existCount = parkingService.count(new LambdaQueryWrapper<ParkingSpace>()
                    .eq(ParkingSpace::getSpaceNo, req.getSpaceNo()));
            if (existCount > 0) return Result.error(400, "车位编号已存在");
            space.setSpaceNo(req.getSpaceNo());
        }
        if (req.getZone() != null) space.setZone(req.getZone());
        if (req.getRowNo() != null) space.setRowNo(req.getRowNo());
        if (req.getColNo() != null) space.setColNo(req.getColNo());
        if (req.getArea() != null) space.setArea(req.getArea());
        if (req.getType() != null && ALLOWED_TYPES.contains(req.getType())) space.setType(req.getType());
        if (req.getPrice() != null) space.setPrice(req.getPrice());
        space.setUpdateTime(LocalDateTime.now());

        parkingService.updateById(space);
        return Result.ok(null);
    }

    // 8. 管理端删除车位（仅允许删除待售状态）
    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ParkingSpace space = parkingService.getById(id);
        if (space == null) return Result.error(404, "车位不存在");
        if (!"FREE".equals(space.getStatus())) return Result.error(400, "只能删除待售状态的车位");

        parkingService.removeById(id);
        return Result.ok(null);
    }

    // 9. 管理端锁定/解锁车位
    @PutMapping("/admin/{id}/lock")
    public Result<Void> lock(@PathVariable Long id, @RequestParam boolean lock) {
        ParkingSpace space = parkingService.getById(id);
        if (space == null) return Result.error(404, "车位不存在");
        if ("SOLD".equals(space.getStatus())) return Result.error(400, "已售车位不可锁定");

        if (lock) {
            space.setStatus("LOCKED");
        } else {
            space.setStatus("FREE");
        }
        space.setUpdateTime(LocalDateTime.now());
        parkingService.updateById(space);
        return Result.ok(null);
    }

    // 10. 管理端统计概览（各状态车位数量）
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", parkingService.count());
        stats.put("free", parkingService.countByStatus("FREE"));
        stats.put("sold", parkingService.countByStatus("SOLD"));
        stats.put("locked", parkingService.countByStatus("LOCKED"));
        stats.put("reserved", parkingService.countByStatus("RESERVED"));
        return Result.ok(stats);
    }

    // 11. 获取所有区域列表（用于前端筛选下拉框）
    @GetMapping("/zones")
    public Result<List<String>> zones() {
        List<ParkingSpace> spaces = parkingService.list(
                new LambdaQueryWrapper<ParkingSpace>()
                        .select(ParkingSpace::getZone)
                        .groupBy(ParkingSpace::getZone));
        List<String> zoneList = spaces.stream().map(ParkingSpace::getZone).toList();
        return Result.ok(zoneList);
    }
}
