package com.example.knowledgeapplication.service.impl;

import com.example.knowledgeapplication.dto.LoginRequest;
import com.example.knowledgeapplication.dto.LoginResponse;
import com.example.knowledgeapplication.dto.PasswordResetRequest;
import com.example.knowledgeapplication.dto.RegisterRequest;
import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.repository.UserRepository;
import com.example.knowledgeapplication.security.JwtTokenProvider;
import com.example.knowledgeapplication.service.EmailService;
import com.example.knowledgeapplication.service.UserService;
import com.example.knowledgeapplication.util.VerificationCodeGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 验证用户名和密码
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        // 2. 设置认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 生成JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // 4. 获取用户信息
        User user = findByEmail(loginRequest.getEmail());

        // 5. 返回登录响应
        return new LoginResponse(token, user.getEmail(), user.getUsername());
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest registerRequest) {
        // 1. 验证密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 2. 检查邮箱是否已存在
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 3. 创建新用户
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmailVerified(false);

        // 4. 保存用户
        userRepository.save(user);

        // 5. 发送验证码
        sendVerificationCode(registerRequest.getEmail());

        // 6. 自动登录并返回token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());
        return login(loginRequest);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public void sendVerificationCode(String email) {
        // 1. 检查用户是否存在
        User user = findByEmail(email);

        // 2. 生成验证码
        String code = VerificationCodeGenerator.generateCode();

        // 3. 发送验证码
        emailService.sendVerificationCode(email, code);
    }

    @Override
    @Transactional
    public void verifyEmail(String email, String code) {
        // 1. 检查用户是否存在
        User user = findByEmail(email);

        // 2. 验证码是否有效
        if (!emailService.verifyCode(email, code)) {
            throw new RuntimeException("验证码无效或已过期");
        }

        // 3. 更新邮箱验证状态
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        // 1. 验证新密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 2. 检查用户是否存在
        User user = findByEmail(request.getEmail());

        // 3. 验证码是否有效
        if (!emailService.verifyCode(request.getEmail(), request.getCode())) {
            throw new RuntimeException("验证码无效或已过期");
        }

        // 4. 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
} 