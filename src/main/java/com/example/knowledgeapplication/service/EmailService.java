package com.example.knowledgeapplication.service;

public interface EmailService {
    void sendVerificationCode(String to, String code);
    boolean verifyCode(String email, String code);
} 