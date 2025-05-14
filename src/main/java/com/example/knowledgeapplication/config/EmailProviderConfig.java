package com.example.knowledgeapplication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "email.providers")
public class EmailProviderConfig {
    private Map<String, EmailProvider> providers;
    private String defaultProvider;

    @Data
    public static class EmailProvider {
        private String host;
        private int port;
        private String username;
        private String password;
        private boolean ssl;
        private boolean tls;
        private String fromName;
        private String fromAddress;
    }
} 