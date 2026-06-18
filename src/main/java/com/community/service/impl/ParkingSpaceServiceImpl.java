package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.ParkingSpace;
import com.community.mapper.ParkingSpaceMapper;
import com.community.service.ParkingSpaceService;
import org.springframework.stereotype.Service;

@Service
public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements ParkingSpaceService {

    @Override
    public Page<ParkingSpace> selectParkingPageWithOwner(Page<ParkingSpace> page, LambdaQueryWrapper<ParkingSpace> qw) {
        return baseMapper.selectParkingPageWithOwner(page, qw);
    }

    @Override
    public long countByStatus(String status) {
        return count(new LambdaQueryWrapper<ParkingSpace>()
                .eq(ParkingSpace::getStatus, status));
    }
}
