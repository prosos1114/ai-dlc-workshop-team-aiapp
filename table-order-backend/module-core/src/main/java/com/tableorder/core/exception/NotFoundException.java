package com.tableorder.core.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

    public NotFoundException(String resourceName, Object identifier) {
        super(resourceName.toUpperCase() + "_NOT_FOUND",
                resourceName + "을(를) 찾을 수 없습니다: " + identifier,
                HttpStatus.NOT_FOUND);
    }
}
