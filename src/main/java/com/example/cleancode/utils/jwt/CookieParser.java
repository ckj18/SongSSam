package com.example.cleancode.utils.jwt;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class CookieParser {
    public Map<String,String> parseCookie(Cookie[] cookies){
        Map<String, String> cookieMap = new HashMap<>();
        List<Cookie> cookieList = Arrays.asList(cookies);
        for (Cookie cookie : cookieList) {
            cookieMap.put(cookie.getName(), cookie.getValue());
            log.info("1");
        }
        return cookieMap;
    }
}
