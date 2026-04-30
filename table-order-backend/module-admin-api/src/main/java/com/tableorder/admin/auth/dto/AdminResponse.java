package com.tableorder.admin.auth.dto;

import com.tableorder.domain.admin.Admin;

import java.time.LocalDateTime;

public record AdminResponse(Long id, Long storeId, String username, LocalDateTime createdAt) {

    public static AdminResponse from(Admin admin) {
        return new AdminResponse(admin.getId(), admin.getStoreId(),
                admin.getUsername(), admin.getCreatedAt());
    }
}
