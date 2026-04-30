package com.tableorder.core.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends BusinessException {

    public InvalidStatusTransitionException(String from, String to) {
        super("INVALID_STATUS_TRANSITION",
                "상태 변경이 허용되지 않습니다: " + from + " → " + to,
                HttpStatus.BAD_REQUEST);
    }
}
