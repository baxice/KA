package com.example.knowledgeapplication.controller;

import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.entity.UserAvatar;
import com.example.knowledgeapplication.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        UserAvatar avatar = userAvatarService.uploadAvatar(user, file);
        String avatarUrl = userAvatarService.getAvatarUrl(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("avatar", avatar);
        response.put("avatarUrl", avatarUrl);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAvatarInfo(@AuthenticationPrincipal User user) {
        UserAvatar avatar = userAvatarService.getAvatar(user);
        String avatarUrl = userAvatarService.getAvatarUrl(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("avatar", avatar);
        response.put("avatarUrl", avatarUrl);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteAvatar(@AuthenticationPrincipal User user) {
        userAvatarService.deleteAvatar(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "头像已删除");
        response.put("avatarUrl", null);
        
        return ResponseEntity.ok().build();
    }
} 