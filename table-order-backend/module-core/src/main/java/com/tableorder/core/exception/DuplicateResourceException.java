package com.tableorder.core.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resourceName, Object identifier) {
        super("DUPLICATE_" + resourceName.toUpperCase(),
                "이미 존재하는 " + resourceName + "입니다: " + identifier,
                HttpStatus.CONFLICT);
    }
}
