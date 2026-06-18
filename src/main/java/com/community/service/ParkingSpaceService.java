package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.ParkingSpace;

public interface ParkingSpaceService extends IService<ParkingSpace> {

    // 联表分页查询（带业主姓名、电话）
    Page<ParkingSpace> selectParkingPageWithOwner(Page<ParkingSpace> page, LambdaQueryWrapper<ParkingSpace> qw);

    // 统计各状态车位数量
    long countByStatus(String status);
}
