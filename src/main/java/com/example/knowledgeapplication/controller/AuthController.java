package com.example.knowledgeapplication.controller;

import com.example.knowledgeapplication.exception.BusinessException;
import com.example.knowledgeapplication.exception.ErrorCode;
import com.example.knowledgeapplication.service.AuthService;
import com.example.knowledgeapplication.util.ResultV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 处理登录请求
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<ResultV0<Map<String, String>>> login(
            @RequestParam String email,
            @RequestParam String password) {
        
        // 调用服务层方法生成JWT
        String token = authService.login(email, password);
        
        // 返回包含JWT的响应
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        
        return ResponseEntity.ok(ResultV0.success(result));
    }

    // 处理注册请求
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<ResultV0<Map<String, String>>> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword) {
        
        // 密码一致性校验
        if(!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAM_VALIDATION_ERROR, "两次密码不一致");
        }
        
        // 调用服务层方法注册并生成JWT
        String token = authService.register(email, password);
        
        // 返回包含JWT的响应
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        
        return ResponseEntity.ok(ResultV0.success(result));
    }
    
    // 前端页面模板
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }
}
