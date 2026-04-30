package com.tableorder.core.exception;

import org.springframework.http.HttpStatus;

public class NoActiveSessionException extends BusinessException {

    public NoActiveSessionException(Long tableId) {
        super("NO_ACTIVE_SESSION",
                "활성 세션이 없습니다. 테이블: " + tableId,
                HttpStatus.BAD_REQUEST);
    }
}
