package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.FacilityBooking;
import com.community.mapper.FacilityBookingMapper;
import com.community.service.FacilityBookingService;
import org.springframework.stereotype.Service;

@Service
public class FacilityBookingServiceImpl extends ServiceImpl<FacilityBookingMapper, FacilityBooking>
        implements FacilityBookingService {

    @Override
    public Page<FacilityBooking> selectBookingPage(Page<FacilityBooking> page, LambdaQueryWrapper<FacilityBooking> qw) {
        return baseMapper.selectBookingPage(page, qw);
    }
}
