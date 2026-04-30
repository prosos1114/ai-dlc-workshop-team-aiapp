package com.tableorder.admin.store.dto;

import com.tableorder.domain.store.Store;

import java.time.LocalDateTime;

public record StoreResponse(Long id, String name, String code, LocalDateTime createdAt) {

    public static StoreResponse from(Store store) {
        return new StoreResponse(store.getId(), store.getName(),
                store.getCode(), store.getCreatedAt());
    }
}
