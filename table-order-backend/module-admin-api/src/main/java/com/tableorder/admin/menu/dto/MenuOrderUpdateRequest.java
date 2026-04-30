package com.tableorder.admin.menu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MenuOrderUpdateRequest(
        @NotNull(message = "메뉴 순서 목록은 필수입니다")
        List<@Valid MenuOrderItem> menuOrders
) {

    public record MenuOrderItem(
            @NotNull(message = "메뉴 ID는 필수입니다")
            Long menuId,

            @NotNull(message = "순서는 필수입니다")
            @Min(value = 0, message = "순서는 0 이상이어야 합니다")
            Integer displayOrder
    ) {
    }
}
