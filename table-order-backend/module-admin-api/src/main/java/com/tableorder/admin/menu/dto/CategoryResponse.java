package com.tableorder.admin.menu.dto;

import com.tableorder.domain.menu.Category;

public record CategoryResponse(Long id, String name, int displayOrder, int menuCount) {

    public static CategoryResponse from(Category category, int menuCount) {
        return new CategoryResponse(category.getId(), category.getName(),
                category.getDisplayOrder(), menuCount);
    }
}
