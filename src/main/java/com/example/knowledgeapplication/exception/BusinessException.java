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

    private final int code;       // 错误码
    private final String detail;  // 补充信息

    // 使用预定义错误码的构造器
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.detail = null;
    }

    // 使用预定义错误码和自定义消息的构造器
    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.detail = detail;
    }
}