package com.tableorder.customer.menu.dto;

import com.tableorder.domain.menu.Category;

public record CategoryResponse(
        Long id,
        String name,
        int displayOrder
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDisplayOrder()
        );
    }
}
