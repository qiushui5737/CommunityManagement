
package com.community.service.impl; // 【修正】这里必须是 impl 包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.Result;
import com.community.config.JwtUtil; // 注意你的 JwtUtil 在 config 包还是 util 包，按需调整
import com.community.entity.SysUser;
import com.community.mapper.SysUserMapper;
import com.community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;
    // 注意：如果 JwtUtil 在 util 包，这里 import 要改
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Result<Map<String, Object>> login(String username, String password, String role) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return Result.error(400, "账号或密码错误");
        }
        if (user.getStatus() == 0) return Result.error(400, "账号已禁用");

        if (role != null && !role.equals(user.getRole())) {
            return Result.error(403, "角色不匹配，请使用正确的角色登录");
        }

        String token = jwtUtil.createToken(user.getId(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("realName", user.getRealName());
        safeUser.put("role", user.getRole());
        data.put("user", safeUser);
        return Result.ok(data);
    }

    @Override
    public Result<Void> register(String username, String password, String realName, String phone) {
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0) {
            return Result.error(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole("OWNER");
        userMapper.insert(user);
        return Result.ok(null);
    }
}
