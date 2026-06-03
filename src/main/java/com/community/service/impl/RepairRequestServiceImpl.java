package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.RepairRequest;
import com.community.mapper.RepairRequestMapper;
import com.community.service.RepairRequestService;
import org.springframework.stereotype.Service;

@Service
public class RepairRequestServiceImpl extends ServiceImpl<RepairRequestMapper, RepairRequest> implements RepairRequestService {
    @Override
    public Page<RepairRequest> selectRepairPageWithOwner(Page<RepairRequest> page, LambdaQueryWrapper<RepairRequest> qw) {
        return baseMapper.selectRepairPageWithOwner(page, qw);
    }
}
