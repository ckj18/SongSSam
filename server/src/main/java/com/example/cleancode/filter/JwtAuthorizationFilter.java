package com.example.cleancode.filter;

import com.example.cleancode.user.dto.JwtDto;
import com.example.cleancode.utils.CustomException.ExceptionCode;
import com.example.cleancode.utils.CustomException.JwtExpireException;
import com.example.cleancode.utils.CustomException.JwtIssueException;
import com.example.cleancode.utils.jwt.JwtService;
import com.example.cleancode.utils.jwt.TokenStatus;
import com.example.cleancode.utils.jwt.TokenValidationResult;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.AuthenticationException;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        log.info("jwtAccessToken : {}",token);
        JwtDto jwtDto = new JwtDto(token,"");
        Authentication auth;
        if(StringUtils.hasText(token)){
            log.info(jwtDto.getAccessToken());
            TokenStatus status = jwtService.validateToken(jwtDto);
            if(status==TokenStatus.TOKEN_VALID){
                try {
                    auth = jwtService.authenticate(jwtDto);
                } catch (AuthenticationException e) {
                    log.info("auth 발급 실패");
                    throw new JwtIssueException(ExceptionCode.JWT_ERROR);
                }
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("AUTH SUCCESS : {}",auth.getName());
            } else if (status==TokenStatus.TOKEN_EXPIRED) {
                log.info("jwt 오류/올바르지 않은 접근");
                request.setAttribute("result",new TokenValidationResult(false,null,null, TokenStatus.TOKEN_EXPIRED,null));
                throw new JwtExpireException(ExceptionCode.EXPIRED_JWT_ERROR);
            }
        }else {
            log.info("No Authorization Header");
            request.setAttribute("result",new TokenValidationResult(false,null,null, TokenStatus.NO_AUTH_HEADER,null));
        }
        filterChain.doFilter(request,response);
    }
}
