package com.example.knowledgeapplication.service;

import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.exception.BusinessException;
import com.example.knowledgeapplication.exception.ErrorCode;
import com.example.knowledgeapplication.repository.UserRepository;
import com.example.knowledgeapplication.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String jwtSecret;
    private final long jwtValidity;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       @Value("${jwt.secretKey}") String jwtSecret,
                       @Value("${jwt.expirationTime}") long jwtValidity) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = jwtSecret;
        this.jwtValidity = jwtValidity;
    }

    /**
     * 用户注册
     * @param email 邮箱
     * @param rawPassword 明文密码
     * @return 生成的JWT令牌
     */
    @Transactional
    public String register(String email, String rawPassword) {
        // 1. 邮箱唯一性校验
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        // 2. 密码加密存储
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));

        // 3. 保存用户
        User savedUser = userRepository.save(user);

        // 4. 生成JWT
        return JwtUtil.generateToken(savedUser.getEmail(), jwtSecret, jwtValidity);
    }

    /**
     * 用户登录
     * @param email 邮箱
     * @param rawPassword 明文密码
     * @return JWT令牌
     */
    public String login(String email, String rawPassword) {
        // 1. 查询用户
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getMessage()));

        // 2. 密码验证
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        // 3. 生成JWT
        return JwtUtil.generateToken(user.getEmail(), jwtSecret, jwtValidity);
    }

    /**
     * 验证JWT有效性
     * @param token JWT令牌
     * @return 解析出的用户邮箱
     */
    public String validateToken(String token) {
        return JwtUtil.parseToken(token, jwtSecret)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN.getMessage()));
    }

    public void validateEmailNotRegistered(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }
    }
}
