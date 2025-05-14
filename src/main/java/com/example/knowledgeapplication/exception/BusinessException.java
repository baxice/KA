package com.example.knowledgeapplication.exception;

import lombok.Getter;

/**
 * 业务异常（已知的、可预见的异常）
 * 示例场景：
 * - 用户输入错误
 * - 违反业务规则
 * - 重复操作等
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;       // 错误码
    private final String detail;     // 补充信息

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.detail = message;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.detail = message;
    }

    public BusinessException(String code, String message, String detail) {
        super(message);
        this.code = code;
        this.detail = detail;
    }
}