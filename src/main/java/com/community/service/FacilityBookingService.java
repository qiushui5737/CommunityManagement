package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.FacilityBooking;

public interface FacilityBookingService extends IService<FacilityBooking> {
    Page<FacilityBooking> selectBookingPage(Page<FacilityBooking> page, LambdaQueryWrapper<FacilityBooking> qw);
}
