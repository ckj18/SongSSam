package com.example.cleancode.filter;

import com.example.cleancode.user.dto.JwtDto;
import com.example.cleancode.utils.jwt.CookieParser;
import com.example.cleancode.utils.jwt.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements Filter {
    private final JwtService jwtService;
    private final CookieParser cookieParser;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    @Value("${jwt.token.expiration-time}")
    private Long tokenMillisecond;
    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshMillisecond;
    public static final String[] blacklist = {"/v2/login","/"};
    public static final String URL ="http://localhost:3000/mypage";
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        log.info("jwt filter");
        String path = ((HttpServletRequest) request).getRequestURI();

        if(!path.startsWith("/admin/generate")|!path.startsWith("/")) {
            Map<String, String> cookieMap = cookieParser.parseCookie(req.getCookies());
            Authentication existAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existAuth != null && existAuth.isAuthenticated()) {
                chain.doFilter(request, response);
                return;
            }
            Authentication auth;
            try {
                String token = cookieMap.get("jwtCookie");
                String refresh = cookieMap.get("jwtRefresh");
                JwtDto jwtDto = new JwtDto(token, refresh);


                //쿠키있으면 userRepository에 저장된 권한 부여
                if (token != null && jwtService.validateToken(jwtDto)) {
                    auth = token != null ? jwtService.authenticate(jwtDto) : null;
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                log.info("filter o.k.");
            } catch (Exception ex) {
                log.error("No cookie" + ex.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
