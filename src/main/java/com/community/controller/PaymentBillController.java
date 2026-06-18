package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.Result;
import com.community.entity.*;
import com.community.mapper.CommunityBuildingMapper;
import com.community.mapper.CommunityHouseMapper;
import com.community.mapper.CommunityUnitMapper;
import com.community.mapper.ParkingSpaceMapper;
import com.community.service.PaymentBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner/bills")
@RequiredArgsConstructor
public class PaymentBillController {
    private final PaymentBillService billService;
    private final CommunityHouseMapper houseMapper;
    private final CommunityUnitMapper unitMapper;
    private final CommunityBuildingMapper buildingMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;

    // 1. 查询当前业主账单（支持按状态筛选）
    @GetMapping
    public Result<List<PaymentBill>> list(@RequestAttribute("userId") Long ownerId,
                                          @RequestParam(required = false) String status) {
        LambdaQueryWrapper<PaymentBill> qw = new LambdaQueryWrapper<>();
        qw.eq(PaymentBill::getOwnerId, ownerId);
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            qw.eq(PaymentBill::getStatus, status.toUpperCase());
        }
        qw.orderByDesc(PaymentBill::getCreateTime);
        List<PaymentBill> bills = billService.list(qw);
        fillHouseLabels(bills);
        return Result.ok(bills);
    }

    // 2. 模拟在线支付
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, @RequestAttribute("userId") Long ownerId) {
        PaymentBill bill = billService.getById(id);
        if (bill == null || !bill.getOwnerId().equals(ownerId)) {
            return Result.error(403, "无权操作该账单");
        }
        if (!"PENDING".equals(bill.getStatus())) {
            return Result.error(400, "账单状态异常，无需重复支付");
        }
        bill.setStatus("PAID");
        bill.setPayTime(LocalDateTime.now());
        billService.updateById(bill);
        return Result.ok(null);
    }

    // 辅助：填充房屋标签 + 车位标签
    private void fillHouseLabels(List<PaymentBill> bills) {
        if (bills == null || bills.isEmpty()) return;

        // 填充车位标签
        List<Long> parkingIds = bills.stream()
                .map(PaymentBill::getParkingSpaceId).filter(id -> id != null).distinct().collect(Collectors.toList());
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
                .map(PaymentBill::getHouseId).filter(id -> id != null).distinct().collect(Collectors.toList());
        if (houseIds.isEmpty()) return;
        Map<Long, CommunityHouse> houseMap = houseMapper.selectBatchIds(houseIds)
                .stream().collect(Collectors.toMap(CommunityHouse::getId, h -> h));
        List<Long> unitIds = houseMap.values().stream().map(CommunityHouse::getUnitId).distinct().collect(Collectors.toList());
        Map<Long, CommunityUnit> unitMap = unitMapper.selectBatchIds(unitIds)
                .stream().collect(Collectors.toMap(CommunityUnit::getId, u -> u));
        List<Long> buildingIds = unitMap.values().stream().map(CommunityUnit::getBuildingId).distinct().collect(Collectors.toList());
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
