package com.tableorder.customer.menu.dto;

import com.tableorder.domain.menu.Menu;

public record MenuResponse(
        Long id,
        Long categoryId,
        String name,
        int price,
        String description,
        String imageUrl,
        int displayOrder
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getCategoryId(),
                menu.getName(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getImageUrl(),
                menu.getDisplayOrder()
        );
    }
}
