package com.example.knowledgeapplication.exception;

/**
 * 错误码分类示例
 * 编码规则：
 * - 1xxx: 用户相关错误
 * - 2xxx: 文档操作错误
 * - 3xxx: 系统校验错误
 */
public enum ErrorCode {

    // 用户模块
    USER_EXISTS(1001, "用户已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    INVALID_CREDENTIALS(1003, "用户名或密码错误"),
    INVALID_TOKEN(1004, "无效的Token"),

    // 文档模块
    DOCUMENT_LIMIT_REACHED(2001, "文档数量达到上限"),
    INVALID_FILE_TYPE(2002, "不支持的文件类型"),

    // 系统校验
    PARAM_VALIDATION_ERROR(3001, "两次密码不一致");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
