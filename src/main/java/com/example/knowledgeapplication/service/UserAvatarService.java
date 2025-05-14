package com.example.knowledgeapplication.service;

import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.entity.UserAvatar;
import org.springframework.web.multipart.MultipartFile;

public interface UserAvatarService {
    UserAvatar uploadAvatar(User user, MultipartFile file);
    UserAvatar getAvatar(User user);
    void deleteAvatar(User user);
    String getAvatarUrl(User user);
} 