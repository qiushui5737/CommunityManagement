package com.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.AccessLog;
import com.community.mapper.AccessLogMapper;
import com.community.service.AccessLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AccessLogServiceImpl extends ServiceImpl<AccessLogMapper, AccessLog> implements AccessLogService {
    @Override
    public Page<AccessLog> selectLogPage(Page<AccessLog> page, String cardNo, String userName, String direction, String accessStatus, Long buildingId) {
        return baseMapper.selectLogPage(page, cardNo, userName, direction, accessStatus, buildingId);
    }

    @Override
    public List<Map<String, Object>> getDailyStats(int days) {
        return baseMapper.selectDailyStats(days);
    }

    @Override
    public List<Map<String, Object>> getLocationStats() {
        return baseMapper.selectLocationStats();
    }
}
