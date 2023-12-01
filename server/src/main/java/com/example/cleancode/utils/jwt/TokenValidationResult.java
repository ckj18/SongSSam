package com.example.cleancode.utils.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class TokenValidationResult {
    private Boolean result;
    private TokenType tokenType;
    private String tokenId;
    private TokenStatus tokenStatus;
    private Exception exception;
}
