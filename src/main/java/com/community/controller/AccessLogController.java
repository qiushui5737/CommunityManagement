package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.AccessLog;
import com.community.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/access-log")
@RequiredArgsConstructor
public class AccessLogController {

    private final AccessLogService logService;

    /** 分页查询进出记录 */
    @GetMapping("/page")
    public Result<Page<AccessLog>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "15") Integer size,
                                        @RequestParam(required = false) String cardNo,
                                        @RequestParam(required = false) String userName,
                                        @RequestParam(required = false) String direction,
                                        @RequestParam(required = false) String accessStatus,
                                        @RequestParam(required = false) Long buildingId) {
        Page<AccessLog> page = new Page<>(current, size);
        return Result.ok(logService.selectLogPage(page, cardNo, userName, direction, accessStatus, buildingId));
    }

    /** 统计概览 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> m = new HashMap<>();
        m.put("total", logService.count());
        m.put("today", logService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AccessLog>()
                .apply("CONVERT(VARCHAR(10), access_time, 120) = CONVERT(VARCHAR(10), GETDATE(), 120)")));
        m.put("denied", logService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AccessLog>()
                .eq(AccessLog::getAccessStatus, "DENIED")));
        m.put("dailyStats", logService.getDailyStats(7));
        m.put("locationStats", logService.getLocationStats());
        return Result.ok(m);
    }
}
