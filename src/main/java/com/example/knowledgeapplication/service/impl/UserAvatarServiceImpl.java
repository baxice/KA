package com.example.knowledgeapplication.service.impl;

import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.entity.UserAvatar;
import com.example.knowledgeapplication.repository.UserAvatarRepository;
import com.example.knowledgeapplication.service.UserAvatarService;
import io.minio.*;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private final UserAvatarRepository userAvatarRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    @Transactional
    public UserAvatar uploadAvatar(User user, MultipartFile file) {
        try {
            // 1. 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("只支持图片文件");
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String fileName = "avatars/" + user.getId() + "/" + UUID.randomUUID() + extension;

            // 3. 上传到MinIO
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());

            // 4. 删除旧头像
            UserAvatar oldAvatar = userAvatarRepository.findByUser(user);
            if (oldAvatar != null) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(oldAvatar.getFilePath())
                    .build());
                userAvatarRepository.delete(oldAvatar);
            }

            // 5. 保存头像信息
            UserAvatar avatar = new UserAvatar();
            avatar.setUser(user);
            avatar.setFileName(originalFilename);
            avatar.setFileType(contentType);
            avatar.setFileSize(file.getSize());
            avatar.setFilePath(fileName);

            return userAvatarRepository.save(avatar);
        } catch (Exception e) {
            log.error("Failed to upload avatar for user: " + user.getId(), e);
            throw new RuntimeException("上传头像失败", e);
        }
    }

    @Override
    public UserAvatar getAvatar(User user) {
        return userAvatarRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void deleteAvatar(User user) {
        try {
            UserAvatar avatar = userAvatarRepository.findByUser(user);
            if (avatar != null) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(avatar.getFilePath())
                    .build());
                userAvatarRepository.delete(avatar);
            }
        } catch (Exception e) {
            log.error("Failed to delete avatar for user: " + user.getId(), e);
            throw new RuntimeException("删除头像失败", e);
        }
    }

    @Override
    public String getAvatarUrl(User user) {
        UserAvatar avatar = userAvatarRepository.findByUser(user);
        if (avatar == null) {
            return null;
        }
        return endpoint + "/" + bucketName + "/" + avatar.getFilePath();
    }
} 