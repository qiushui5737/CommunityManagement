package com.community.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简易 IP 限流过滤器 — 防止单 IP 恶意刷接口
 * 规则：同一 IP 在滑动窗口（1秒）内最多允许 N 次请求
 * 超出则直接返回 429 Too Many Requests
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    /** 每秒单 IP 最大请求数 */
    private static final int MAX_REQUESTS_PER_SECOND = 50;

    /** IP → 计数器（每秒重置） */
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = getClientIp(request);

        WindowCounter counter = counters.computeIfAbsent(ip, k -> new WindowCounter());
        if (!counter.tryAcquire()) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"msg\":\"请求过于频繁，请稍后重试\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    /** 排除静态资源和健康检查 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/uploads") || path.equals("/health");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 取第一个IP（Nginx转发链第一个）
        return ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }

    /**
     * 滑动窗口计数器：每秒重置一次
     */
    private static class WindowCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart > 1000) {
                // 新窗口，重置
                synchronized (this) {
                    if (now - windowStart > 1000) {
                        count.set(0);
                        windowStart = now;
                    }
                }
            }
            return count.incrementAndGet() <= MAX_REQUESTS_PER_SECOND;
        }
    }
}
