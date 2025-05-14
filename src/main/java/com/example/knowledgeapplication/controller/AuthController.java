package com.example.knowledgeapplication.controller;

import com.example.knowledgeapplication.dto.EmailVerificationRequest;
import com.example.knowledgeapplication.dto.LoginRequest;
import com.example.knowledgeapplication.dto.LoginResponse;
import com.example.knowledgeapplication.dto.PasswordResetRequest;
import com.example.knowledgeapplication.dto.RegisterRequest;
import com.example.knowledgeapplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestParam String email) {
        userService.sendVerificationCode(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "验证码已发送");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        userService.verifyEmail(request.getEmail(), request.getCode());
        Map<String, String> response = new HashMap<>();
        response.put("message", "邮箱验证成功");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "密码重置成功");
        return ResponseEntity.ok(response);
    }
}
