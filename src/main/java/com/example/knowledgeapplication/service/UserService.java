package com.example.knowledgeapplication.service;

import com.example.knowledgeapplication.dto.LoginRequest;
import com.example.knowledgeapplication.dto.LoginResponse;
import com.example.knowledgeapplication.dto.PasswordResetRequest;
import com.example.knowledgeapplication.dto.RegisterRequest;
import com.example.knowledgeapplication.entity.User;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse register(RegisterRequest registerRequest);
    User findByEmail(String email);
    void sendVerificationCode(String email);
    void verifyEmail(String email, String code);
    void resetPassword(PasswordResetRequest request);
} 