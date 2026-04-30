package com.tableorder.admin.auth.dto;

public record TokenResponse(String token, long expiresIn) {
}
