package com.example.knowledgeapplication.util;

import java.util.Random;

public class VerificationCodeGenerator {
    private static final String NUMBERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    public static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return code.toString();
    }
} 