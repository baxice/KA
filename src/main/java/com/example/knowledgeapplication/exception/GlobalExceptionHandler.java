package com.example.knowledgeapplication.exception;

import com.example.knowledgeapplication.util.ResultV0;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e,
                                        HttpServletRequest request,
                                        HttpSession session) {
        session.setAttribute("error", e.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResultV0<String>> handleBusinessException(BusinessException e) {
        ResultV0<String> result = ResultV0.error(e.getCode(), e.getMessage(), e.getDetail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultV0<String>> handleAuthenticationException(AuthenticationException e) {
        ResultV0<String> result = ResultV0.error(401, "认证失败", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultV0<String>> handleAccessDeniedException(AccessDeniedException e) {
        ResultV0<String> result = ResultV0.error(403, "没有权限", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultV0<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ResultV0<Map<String, String>> result = ResultV0.error(400, "参数验证失败", errors);
        return ResponseEntity.badRequest().body(result);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultV0<String>> handleRuntimeException(RuntimeException ex) {
        ResultV0<String> result = ResultV0.error(500, "服务器内部错误", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultV0<String>> handleException(Exception e) {
        ResultV0<String> result = ResultV0.error(500, "服务器内部错误", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
