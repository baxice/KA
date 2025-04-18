package com.example.knowledgeapplication.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 * 1. 从请求头提取Token
 * 2. 验证Token有效性
 * 3. 设置Spring Security上下文
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String secretKey;

    public JwtAuthenticationFilter(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从Header提取Token
        String token = extractToken(request);

        if (token != null) {
            try {
                // 2. 验证并解析Token
                Claims claims = parseToken(token);

                // 3. 构建Authentication对象
                Authentication auth = createAuthentication(claims);

                // 4. 设置安全上下文
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // Token验证失败时清除上下文
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Authentication createAuthentication(Claims claims) {
        // 从claims中提取用户信息（根据你的JWT实际结构调整）
        String username = claims.getSubject();

        // 示例：从claims获取角色（需与JWT生成逻辑一致）
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER") // 默认角色
        );

        return new UsernamePasswordAuthenticationToken(
                username,
                null, // credentials通常为null
                authorities);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // 放行登录/注册等无需认证的端点
        return request.getServletPath().startsWith("/auth");
    }
}
