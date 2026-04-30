package com.tableorder.core.exception;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends BusinessException {

    public AccountLockedException() {
        super("ACCOUNT_LOCKED",
                "계정이 일시적으로 잠겼습니다. 잠시 후 다시 시도해주세요",
                HttpStatus.TOO_MANY_REQUESTS);
    }
}
