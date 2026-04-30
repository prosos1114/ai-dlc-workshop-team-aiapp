package com.tableorder.core.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS",
                "매장 식별자, 사용자명 또는 비밀번호가 올바르지 않습니다",
                HttpStatus.UNAUTHORIZED);
    }
}
