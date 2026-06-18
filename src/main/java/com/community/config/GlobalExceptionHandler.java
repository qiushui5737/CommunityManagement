package com.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理器 — 防止未捕获异常导致服务崩溃
 * 高并发下尤其重要：一条慢SQL抛异常后如果不捕获，
 * 会占用整个线程直到超时，最终线程池耗尽服务不可用。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 数据库异常 → 503（服务暂时不可用）
     * 连接池耗尽、超时、SQL执行失败等
     */
    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public ResponseEntity<Map<String, Object>> handleDbException(Exception e) {
        log.error("数据库异常: {}", e.getMessage());
        return buildResponse(503, "系统繁忙，请稍后重试");
    }

    /**
     * 参数校验异常 → 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException e) {
        return buildResponse(400, e.getMessage());
    }

    /**
     * 兜底：所有未处理的异常 → 500
     * 防止堆栈信息泄漏给前端
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception e) {
        log.error("未捕获异常: ", e);
        return buildResponse(500, "服务器内部错误");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(int code, String msg) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("msg", msg);
        return ResponseEntity.status(code >= 500 ? HttpStatus.INTERNAL_SERVER_ERROR
                : code == 503 ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST).body(body);
    }
}
