package com.tableorder.core.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(String code, String message, LocalDateTime timestamp) {
}
