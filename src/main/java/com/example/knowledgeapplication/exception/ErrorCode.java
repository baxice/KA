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
    ACCESS_DENIED(1005, "没有权限访问"),
    EMAIL_ALREADY_EXISTS(1006, "邮箱已被注册"),
    USERNAME_ALREADY_EXISTS(1007, "用户名已被使用"),
    INVALID_VERIFICATION_CODE(1008, "验证码无效或已过期"),
    UNAUTHORIZED(1009, "未授权访问"),
    FORBIDDEN(1010, "禁止访问"),

    // 文档模块
    DOCUMENT_NOT_FOUND(2001, "文档不存在"),
    DOCUMENT_UPLOAD_FAILED(2002, "文档上传失败"),
    DOCUMENT_DELETE_FAILED(2003, "文档删除失败"),
    DOCUMENT_UPDATE_FAILED(2004, "文档更新失败"),
    INVALID_FILE_TYPE(2005, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(2006, "文件大小超出限制"),

    // 系统校验
    PARAM_VALIDATION_ERROR(3001, "参数验证失败"),
    INTERNAL_SERVER_ERROR(3002, "服务器内部错误");

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

    @Override
    public String toString() {
        return this.message;
    }
}
