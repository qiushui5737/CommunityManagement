package com.community.service;

import com.community.common.Result;
import java.util.Map;

public interface AuthService {
    Result<Map<String, Object>> login(String username, String password, String role);
    Result<Void> register(String username, String password, String realName, String phone);
}
