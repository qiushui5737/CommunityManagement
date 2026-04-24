package com.community.controller;
import com.community.common.Result;
import com.community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> req) {
        return authService.login(req.get("username"), req.get("password"), req.get("role"));
    }
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> req) {
        return authService.register(req.get("username"), req.get("password"), req.get("realName"), req.get("phone"));
    }
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestAttribute("userId") Long userId) {
        // 实际项目中应查询数据库，此处简化
        return Result.ok(Map.of("id", userId, "role", "OWNER"));
    }
}
