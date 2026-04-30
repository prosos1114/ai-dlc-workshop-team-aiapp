package com.tableorder.core.security;

public record AuthenticatedUser(Long subjectId, Long storeId, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isTable() {
        return "TABLE".equals(role);
    }
}
