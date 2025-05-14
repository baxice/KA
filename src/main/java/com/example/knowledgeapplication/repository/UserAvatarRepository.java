package com.example.knowledgeapplication.repository;

import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.entity.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
    UserAvatar findByUser(User user);
} 