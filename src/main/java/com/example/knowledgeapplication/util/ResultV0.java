package com.example.knowledgeapplication.util;

import lombok.Data;

@Data
public class ResultV0<T> {
    private Integer code;
    private String message;
    private T data;
    private String detail;

    private ResultV0(Integer code, String message, T data, String detail) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.detail = detail;
    }

    public static <T> ResultV0<T> success(T data) {
        return new ResultV0<>(200, "success", data, null);
    }

    public static <T> ResultV0<T> error(Integer code, String message) {
        return new ResultV0<>(code, message, null, null);
    }

    public static <T> ResultV0<T> error(Integer code, String message, String detail) {
        return new ResultV0<>(code, message, null, detail);
    }
}
