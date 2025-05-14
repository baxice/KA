package com.example.knowledgeapplication.security;

import com.example.knowledgeapplication.config.JwtConfig;
import io.jsonwebtoken.Claims;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 1. 从请求头提取Token
 * 2. 验证Token有效性
 * 3. 设置Spring Security上下文
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, JwtConfig jwtConfig) {
        this.tokenProvider = tokenProvider;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从Header提取Token
        String token = extractToken(request);

        if (token != null && tokenProvider.validateToken(token)) {
            try {
                // 2. 验证并解析Token
                String username = tokenProvider.getUsernameFromToken(token);
                Claims claims = tokenProvider.getClaimsFromToken(token);
                
                // 3. 构建Authentication对象
                List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get("roles", String.class)
                        .split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

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
        String bearerToken = request.getHeader(jwtConfig.getHeaderString());
        if (bearerToken != null && bearerToken.startsWith(jwtConfig.getTokenPrefix())) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // 放行登录/注册等无需认证的端点
        return request.getServletPath().startsWith("/auth");
    }
}
