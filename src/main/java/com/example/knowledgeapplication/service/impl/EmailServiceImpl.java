package com.example.knowledgeapplication.service.impl;

import com.example.knowledgeapplication.config.EmailProviderConfig;
import com.example.knowledgeapplication.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final Map<String, JavaMailSender> mailSenders = new ConcurrentHashMap<>();
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> codeExpirationTimes = new ConcurrentHashMap<>();
    private final EmailProviderConfig emailProviderConfig;

    @Value("${email.verification.code-expiration}")
    private int codeExpiration;

    @Value("${email.verification.template}")
    private String emailTemplate;

    public EmailServiceImpl(EmailProviderConfig emailProviderConfig) {
        this.emailProviderConfig = emailProviderConfig;
        initializeMailSenders();
    }

    private void initializeMailSenders() {
        emailProviderConfig.getProviders().forEach((providerName, provider) -> {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(provider.getHost());
            mailSender.setPort(provider.getPort());
            mailSender.setUsername(provider.getUsername());
            mailSender.setPassword(provider.getPassword());

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            if (provider.isSsl()) {
                props.put("mail.smtp.ssl.enable", "true");
            }
            if (provider.isTls()) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
            }

            mailSenders.put(providerName, mailSender);
        });
    }

    private JavaMailSender getMailSender() {
        String providerName = emailProviderConfig.getDefaultProvider();
        JavaMailSender mailSender = mailSenders.get(providerName);
        if (mailSender == null) {
            throw new RuntimeException("No mail sender configured for provider: " + providerName);
        }
        return mailSender;
    }

    private String getFromAddress() {
        String providerName = emailProviderConfig.getDefaultProvider();
        EmailProviderConfig.EmailProvider provider = emailProviderConfig.getProviders().get(providerName);
        if (provider == null) {
            throw new RuntimeException("No email provider configured: " + providerName);
        }
        return provider.getFromAddress();
    }

    private String getFromName() {
        String providerName = emailProviderConfig.getDefaultProvider();
        EmailProviderConfig.EmailProvider provider = emailProviderConfig.getProviders().get(providerName);
        if (provider == null) {
            throw new RuntimeException("No email provider configured: " + providerName);
        }
        return provider.getFromName();
    }

    @Override
    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = getMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(getFromAddress(), getFromName());
            helper.setTo(to);
            helper.setSubject("邮箱验证码");
            helper.setText(String.format(emailTemplate, code), true);

            getMailSender().send(message);

            // 保存验证码和过期时间
            verificationCodes.put(to, code);
            codeExpirationTimes.put(to, System.currentTimeMillis() + (codeExpiration * 1000L));
            
            log.info("Verification code sent to: {}", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send verification code to: " + to, e);
            throw new RuntimeException("Failed to send verification code", e);
        }
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        Long expirationTime = codeExpirationTimes.get(email);

        if (savedCode == null || expirationTime == null) {
            return false;
        }

        // 检查验证码是否过期
        if (System.currentTimeMillis() > expirationTime) {
            verificationCodes.remove(email);
            codeExpirationTimes.remove(email);
            return false;
        }

        // 验证成功后删除验证码
        if (savedCode.equals(code)) {
            verificationCodes.remove(email);
            codeExpirationTimes.remove(email);
            return true;
        }

        return false;
    }
} 