package com.example.cleancode.utils.CustomException;

import com.example.cleancode.utils.CustomException.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
public class NoUserSongException extends RuntimeException{
    private final ExceptionCode exceptionCode;
    public NoUserSongException(ExceptionCode exceptionCode){
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
