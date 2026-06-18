package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.*;
import com.community.mapper.*;
import com.community.service.PaymentBillService;
import com.community.service.PropertyFeeItemService;
import com.community.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/bills")
@RequiredArgsConstructor
public class AdminBillController {
    private final PaymentBillService billService;
    private final PropertyFeeItemService feeItemService;
    private final SysUserService userService;
    private final CommunityHouseMapper houseMapper;
    private final CommunityUnitMapper unitMapper;
    private final CommunityBuildingMapper buildingMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;

    // 1. 账单列表查询（支持按项目、状态筛选）
    @GetMapping("/page")
    public Result<Page<PaymentBill>> page(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) Long feeItemId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String ownerKeyword) {
        LambdaQueryWrapper<PaymentBill> qw = new LambdaQueryWrapper<>();
        if (feeItemId != null) qw.eq(PaymentBill::getFeeItemId, feeItemId);
        if (status != null && !status.isBlank()) qw.eq(PaymentBill::getStatus, status);
        qw.orderByDesc(PaymentBill::getCreateTime);

        Page<PaymentBill> page = billService.page(new Page<>(current, size), qw);
        fillHouseLabels(page.getRecords());
        return Result.ok(page);
    }

    // 2. 查询可收费对象列表（房屋 或 车位）
    @GetMapping("/eligible-houses")
    public Result<List<Map<String, Object>>> eligibleHouses(@RequestParam(required = false) Long feeItemId) {
        boolean isParkingFee = false;
        if (feeItemId != null) {
            PropertyFeeItem feeItem = feeItemService.getById(feeItemId);
            isParkingFee = feeItem != null && feeItem.getItemName() != null && feeItem.getItemName().contains("车位");
        }

        List<Map<String, Object>> result = new ArrayList<>();

        if (isParkingFee) {
            // 车位管理费：返回所有已售/预订的车位
            List<ParkingSpace> spaces = parkingSpaceMapper.selectList(
                    new LambdaQueryWrapper<ParkingSpace>()
                            .isNotNull(ParkingSpace::getOwnerId)
                            .in(ParkingSpace::getStatus, "SOLD", "RESERVED")
                            .orderByAsc(ParkingSpace::getSpaceNo));
            if (spaces.isEmpty()) return Result.ok(Collections.emptyList());

            List<Long> ownerIds = spaces.stream().map(ParkingSpace::getOwnerId).distinct().collect(Collectors.toList());
            Map<Long, SysUser> ownerMap = userService.listByIds(ownerIds)
                    .stream().collect(Collectors.toMap(SysUser::getId, u -> u));

            for (ParkingSpace ps : spaces) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", ps.getId());
                m.put("type", "parking");
                m.put("ownerId", ps.getOwnerId());
                m.put("label", ps.getSpaceNo());
                SysUser owner = ownerMap.get(ps.getOwnerId());
                m.put("ownerName", owner != null && owner.getRealName() != null ? owner.getRealName().trim() : "业主" + ps.getOwnerId());
                result.add(m);
            }
        } else {
            // 非车位费：返回所有已绑定业主的房屋
            List<CommunityHouse> houses = houseMapper.selectList(
                    new LambdaQueryWrapper<CommunityHouse>().isNotNull(CommunityHouse::getOwnerId));
            if (houses.isEmpty()) return Result.ok(Collections.emptyList());

            List<Long> unitIds = houses.stream().map(CommunityHouse::getUnitId).distinct().collect(Collectors.toList());
            Map<Long, CommunityUnit> unitMap = unitMapper.selectBatchIds(unitIds)
                    .stream().collect(Collectors.toMap(CommunityUnit::getId, u -> u));
            List<Long> buildingIds = unitMap.values().stream().map(CommunityUnit::getBuildingId).distinct().collect(Collectors.toList());
            Map<Long, CommunityBuilding> buildingMap = buildingMapper.selectBatchIds(buildingIds)
                    .stream().collect(Collectors.toMap(CommunityBuilding::getId, b -> b));
            List<Long> ownerIds = houses.stream().map(CommunityHouse::getOwnerId).distinct().collect(Collectors.toList());
            Map<Long, SysUser> ownerMap = userService.listByIds(ownerIds)
                    .stream().collect(Collectors.toMap(SysUser::getId, u -> u));

            for (CommunityHouse h : houses) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", h.getId());
                m.put("type", "house");
                m.put("ownerId", h.getOwnerId());
                CommunityUnit u = unitMap.get(h.getUnitId());
                CommunityBuilding b = u != null ? buildingMap.get(u.getBuildingId()) : null;
                String bNo = b != null && b.getBuildingNo() != null ? b.getBuildingNo() : "";
                String uNo = u != null && u.getUnitNo() != null ? u.getUnitNo() : "";
                String rNo = h.getRoomNo() != null ? h.getRoomNo() : "";
                m.put("label", bNo + "-" + uNo + "-" + rNo);
                SysUser owner = ownerMap.get(h.getOwnerId());
                m.put("ownerName", owner != null && owner.getRealName() != null ? owner.getRealName().trim() : "业主" + h.getOwnerId());
                result.add(m);
            }
        }
        return Result.ok(result);
    }

    // 3. 批量生成账单（房屋/车位维度）
    @PostMapping("/generate")
    public Result<Void> generateBills(@RequestParam Long feeItemId,
                                      @RequestParam(required = false) String dueDate,
                                      @RequestParam(required = false) String targetIds,
                                      @RequestParam(defaultValue = "house") String targetType) {
        PropertyFeeItem item = feeItemService.getById(feeItemId);
        if (item == null) return Result.error(404, "收费项目不存在");
        if (item.getStatus() == 0) return Result.error(400, "该项目已停用，无法生成账单");

        // 截止日期
        final LocalDate finalDueDate;
        if (dueDate != null && !dueDate.isBlank()) {
            finalDueDate = LocalDate.parse(dueDate);
        } else {
            int addMonths = 1;
            if ("QUARTER".equals(item.getCycle())) addMonths = 3;
            else if ("YEAR".equals(item.getCycle())) addMonths = 12;
            finalDueDate = LocalDate.now().plusMonths(addMonths);
        }

        // 解析目标ID列表
        final List<Long> ids;
        if (targetIds != null && !targetIds.isBlank()) {
            ids = Arrays.stream(targetIds.split(","))
                    .map(String::trim).map(Long::parseLong).collect(Collectors.toList());
        } else {
            ids = Collections.emptyList();
        }

        if ("parking".equals(targetType)) {
            // 车位维度生成
            List<ParkingSpace> spaces;
            if (!ids.isEmpty()) {
                spaces = parkingSpaceMapper.selectBatchIds(ids);
            } else {
                spaces = parkingSpaceMapper.selectList(
                        new LambdaQueryWrapper<ParkingSpace>()
                                .isNotNull(ParkingSpace::getOwnerId)
                                .in(ParkingSpace::getStatus, "SOLD", "RESERVED"));
            }
            if (spaces.isEmpty()) return Result.ok(null);

            // 防重复：查本期已有车位账单
            Set<Long> existingParkingIds = billService.list(new LambdaQueryWrapper<PaymentBill>()
                            .eq(PaymentBill::getFeeItemId, feeItemId)
                            .ge(PaymentBill::getDueDate, LocalDate.now()))
                    .stream().map(PaymentBill::getParkingSpaceId)
                    .filter(pid -> pid != null).collect(Collectors.toSet());

            for (ParkingSpace ps : spaces) {
                if (existingParkingIds.contains(ps.getId())) continue;
                PaymentBill bill = new PaymentBill();
                bill.setOwnerId(ps.getOwnerId());
                bill.setParkingSpaceId(ps.getId());
                bill.setFeeItemId(feeItemId);
                bill.setAmount(item.getAmount());
                bill.setStatus("PENDING");
                bill.setDueDate(finalDueDate);
                bill.setCreateTime(LocalDateTime.now());
                billService.save(bill);
            }
        } else {
            // 房屋维度生成
            List<CommunityHouse> houses;
            if (!ids.isEmpty()) {
                houses = houseMapper.selectBatchIds(ids);
            } else {
                houses = houseMapper.selectList(
                        new LambdaQueryWrapper<CommunityHouse>().isNotNull(CommunityHouse::getOwnerId));
            }
            if (houses.isEmpty()) return Result.ok(null);

            Set<Long> existingHouseIds = billService.list(new LambdaQueryWrapper<PaymentBill>()
                            .eq(PaymentBill::getFeeItemId, feeItemId)
                            .ge(PaymentBill::getDueDate, LocalDate.now()))
                    .stream().map(PaymentBill::getHouseId)
                    .filter(hid -> hid != null).collect(Collectors.toSet());

            for (CommunityHouse h : houses) {
                if (existingHouseIds.contains(h.getId())) continue;
                PaymentBill bill = new PaymentBill();
                bill.setOwnerId(h.getOwnerId());
                bill.setHouseId(h.getId());
                bill.setFeeItemId(feeItemId);
                bill.setAmount(item.getAmount());
                bill.setStatus("PENDING");
                bill.setDueDate(finalDueDate);
                bill.setCreateTime(LocalDateTime.now());
                billService.save(bill);
            }
        }
        return Result.ok(null);
    }

    // 4. 手动修改账单状态
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        PaymentBill bill = new PaymentBill();
        bill.setId(id);
        bill.setStatus(status);
        if ("PAID".equals(status)) bill.setPayTime(LocalDateTime.now());
        billService.updateById(bill);
        return Result.ok(null);
    }

    // 辅助：为账单列表填充房屋标签/车位标签
    private void fillHouseLabels(List<PaymentBill> bills) {
        if (bills == null || bills.isEmpty()) return;

        // 填充车位标签
        List<Long> parkingIds = bills.stream()
                .map(PaymentBill::getParkingSpaceId)
                .filter(id -> id != null).distinct().collect(Collectors.toList());
        if (!parkingIds.isEmpty()) {
            Map<Long, ParkingSpace> parkingMap = parkingSpaceMapper.selectBatchIds(parkingIds)
                    .stream().collect(Collectors.toMap(ParkingSpace::getId, p -> p));
            for (PaymentBill bill : bills) {
                if (bill.getParkingSpaceId() == null) continue;
                ParkingSpace ps = parkingMap.get(bill.getParkingSpaceId());
                if (ps != null) bill.setParkingLabel(ps.getSpaceNo());
            }
        }

        // 填充房屋标签
        List<Long> houseIds = bills.stream()
                .map(PaymentBill::getHouseId)
                .filter(id -> id != null).distinct().collect(Collectors.toList());
        if (houseIds.isEmpty()) return;

        Map<Long, CommunityHouse> houseMap = houseMapper.selectBatchIds(houseIds)
                .stream().collect(Collectors.toMap(CommunityHouse::getId, h -> h));
        List<Long> unitIds = houseMap.values().stream()
                .map(CommunityHouse::getUnitId).distinct().collect(Collectors.toList());
        Map<Long, CommunityUnit> unitMap = unitMapper.selectBatchIds(unitIds)
                .stream().collect(Collectors.toMap(CommunityUnit::getId, u -> u));
        List<Long> buildingIds = unitMap.values().stream()
                .map(CommunityUnit::getBuildingId).distinct().collect(Collectors.toList());
        Map<Long, CommunityBuilding> buildingMap = buildingMapper.selectBatchIds(buildingIds)
                .stream().collect(Collectors.toMap(CommunityBuilding::getId, b -> b));

        for (PaymentBill bill : bills) {
            if (bill.getHouseId() == null) continue;
            CommunityHouse h = houseMap.get(bill.getHouseId());
            if (h == null) continue;
            CommunityUnit u = unitMap.get(h.getUnitId());
            CommunityBuilding b = u != null ? buildingMap.get(u.getBuildingId()) : null;
            String bNo = b != null && b.getBuildingNo() != null ? b.getBuildingNo() : "";
            String uNo = u != null && u.getUnitNo() != null ? u.getUnitNo() : "";
            String rNo = h.getRoomNo() != null ? h.getRoomNo() : "";
            bill.setHouseLabel(bNo + "-" + uNo + "-" + rNo);
        }
    }
}
