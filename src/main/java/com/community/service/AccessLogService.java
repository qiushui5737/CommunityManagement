package com.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.AccessLog;

import java.util.List;
import java.util.Map;

public interface AccessLogService extends IService<AccessLog> {
    Page<AccessLog> selectLogPage(Page<AccessLog> page, String cardNo, String userName, String direction, String accessStatus, Long buildingId);
    List<Map<String, Object>> getDailyStats(int days);
    List<Map<String, Object>> getLocationStats();
}
