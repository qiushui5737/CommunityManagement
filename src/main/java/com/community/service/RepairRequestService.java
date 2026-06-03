package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.RepairRequest;

public interface RepairRequestService extends IService<RepairRequest> {
    Page<RepairRequest> selectRepairPageWithOwner(Page<RepairRequest> page, LambdaQueryWrapper<RepairRequest> qw);

}
