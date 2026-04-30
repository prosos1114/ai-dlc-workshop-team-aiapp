package com.tableorder.admin.menu.dto;

import com.tableorder.domain.menu.Menu;

public record MenuResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        int price,
        String description,
        String imageUrl,
        int displayOrder
) {

    public static MenuResponse from(Menu menu, String categoryName) {
        return new MenuResponse(menu.getId(), menu.getCategoryId(), categoryName,
                menu.getName(), menu.getPrice(), menu.getDescription(),
                menu.getImageUrl(), menu.getDisplayOrder());
    }
}
