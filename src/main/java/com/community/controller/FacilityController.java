package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Facility;
import com.community.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    private static final List<String> CATEGORIES =
            Arrays.asList("运动器材", "工具设备", "文娱用品", "清洁工具", "其他");

    // ====== 公共接口（业主 & 管理员共用） ======

    /**
     * 设施列表（分页 + 分类筛选）
     * 业主端用于浏览可借用设施；管理端用于设施管理列表
     */
    @GetMapping("/page")
    public Result<Page<Facility>> page(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "12") Integer size,
                                       @RequestParam(required = false) String category,
                                       @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Facility> qw = new LambdaQueryWrapper<>();
        if (category != null && !category.isBlank()) qw.eq(Facility::getCategory, category);
        if (keyword  != null && !keyword.isBlank())  qw.like(Facility::getName, keyword);
        qw.orderByDesc(Facility::getCreateTime);
        return Result.ok(facilityService.page(new Page<>(current, size), qw));
    }

    /**
     * 设施详情（单条）
     */
    @GetMapping("/{id}")
    public Result<Facility> detail(@PathVariable Long id) {
        Facility f = facilityService.getById(id);
        if (f == null) return Result.error(404, "设施不存在");
        return Result.ok(f);
    }

    /**
     * 获取所有设施分类列表（前端下拉选项用）
     */
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.ok(CATEGORIES);
    }

    /**
     * 设施状态统计（首页卡片用）
     */
    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Map<String, Long> counts = facilityService.list().stream()
                .collect(Collectors.groupingBy(Facility::getStatus, Collectors.counting()));
        return Result.ok(counts);
    }

    // ====== 管理端 CRUD ======

    /**
     * 新增设施
     */
    @PostMapping
    public Result<Void> add(@RequestBody Facility facility) {
        facility.setStatus("AVAILABLE");
        facility.setCreateTime(LocalDateTime.now());
        facility.setUpdateTime(LocalDateTime.now());
        facilityService.save(facility);
        return Result.ok(null);
    }

    /**
     * 编辑设施
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Facility facility) {
        facility.setId(id);
        facility.setUpdateTime(LocalDateTime.now());
        facilityService.updateById(facility);
        return Result.ok(null);
    }

    /**
     * 删除设施（软删：标记为 RETIRED）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Facility f = new Facility();
        f.setId(id);
        f.setStatus("RETIRED");
        f.setUpdateTime(LocalDateTime.now());
        facilityService.updateById(f);
        return Result.ok(null);
    }
}
