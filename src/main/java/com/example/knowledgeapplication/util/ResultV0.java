package com.example.knowledgeapplication.util;

import lombok.Data;

@Data
public class ResultV0<T> {
    private Integer code;
    private String message;
    private T data;
    private String detail;

    public static <T> ResultV0<T> success() {
        return success(null);
    }

    public static <T> ResultV0<T> success(T data) {
        ResultV0<T> result = new ResultV0<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> ResultV0<T> error(Integer code, String message) {
        return error(code, message, null);
    }

    public static <T> ResultV0<T> error(Integer code, String message, T data) {
        return error(code, message, data, null);
    }

    public static <T> ResultV0<T> error(Integer code, String message, T data, String detail) {
        ResultV0<T> result = new ResultV0<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setDetail(detail);
        return result;
    }

    public static <T> ResultV0<T> error(String code, String message, String detail) {
        ResultV0<T> result = new ResultV0<>();
        result.setCode(500);
        result.setMessage(message);
        result.setDetail(detail);
        return result;
    }
}
