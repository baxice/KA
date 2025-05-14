package com.example.knowledgeapplication.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long id;
    private String title;
    private String originalFilename;
    private String fileType;
    private Long fileSize;
    private String mimeType;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username; // 文档所有者用户名
} 