package com.tableorder.customer.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "메뉴 ID는 필수입니다")
        Long menuId,

        @NotNull(message = "수량은 필수입니다")
        @Min(value = 1, message = "수량은 최소 1개입니다")
        @Max(value = 99, message = "수량은 최대 99개입니다")
        Integer quantity
) {
}
