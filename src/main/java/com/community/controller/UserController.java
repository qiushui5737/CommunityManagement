package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.CommunityBuilding;
import com.community.entity.CommunityHouse;
import com.community.entity.SysUser;
import com.community.mapper.CommunityBuildingMapper;
import com.community.mapper.CommunityHouseMapper;
import com.community.mapper.SysUserMapper;
import com.community.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final SysUserService userService;
    private final SysUserMapper userMapper;          // 👈 注入 Mapper 替代 userService.baseMapper
    private final CommunityHouseMapper houseMapper;
    private final CommunityBuildingMapper buildingMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 1. 分页查询（联表）
    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword);
        }
        qw.orderByDesc(SysUser::getCreateTime);
        return Result.ok(userMapper.selectUserPageWithHouse(new Page<>(current, size), qw));
    }

    // 2. 新增
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        user.setPassword(encoder.encode("123456"));
        user.setStatus(1);
        userService.save(user);
        bindHouse(user.getId(), user.getHouseId());
        return Result.ok(null);
    }

    // 3. 编辑
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(userService.getById(id).getPassword());
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        bindHouse(id, user.getHouseId());
        return Result.ok(null);
    }

    // 4. 删除 & 状态切换（保持原有逻辑）
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) { userService.removeById(id); return Result.ok(null); }
    @PutMapping("/status/{id}") public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser u = new SysUser(); u.setId(id); u.setStatus(status); userService.updateById(u); return Result.ok(null);
    }

    // 5. 房屋绑定逻辑
    private void bindHouse(Long userId, Long houseId) {
        if (houseId == null) return;
        houseMapper.update(null, new UpdateWrapper<CommunityHouse>().eq("owner_id", userId).set("owner_id", null));
        CommunityHouse h = new CommunityHouse();
        h.setId(houseId);
        h.setOwnerId(userId);
        houseMapper.updateById(h);
    }

    // 6. 级联下拉数据源

    @GetMapping("/buildings")
    public Result<List<Map<String, Object>>> getBuildings() {
        List<Map<String, Object>> list = buildingMapper.selectList(null).stream()
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", b.getId());
                    map.put("name", b.getBuildingNo() + " - " + b.getName());
                    return map;
                }).collect(Collectors.toList());
        return Result.ok(list);
    }

    @GetMapping("/houses")
    public Result<List<Map<String, Object>>> getHouses(@RequestParam Long buildingId) {
        List<Map<String, Object>> list = houseMapper.selectList(
                new LambdaQueryWrapper<CommunityHouse>()
                        .inSql(CommunityHouse::getUnitId, "SELECT id FROM community_unit WHERE building_id = " + buildingId)
        ).stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", h.getId());
            map.put("roomNo", h.getRoomNo());
            return map;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }
}
