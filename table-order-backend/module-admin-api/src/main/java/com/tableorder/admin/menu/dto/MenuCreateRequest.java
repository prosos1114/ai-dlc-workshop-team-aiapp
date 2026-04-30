package com.tableorder.admin.menu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuCreateRequest(
        @NotNull(message = "카테고리 ID는 필수입니다")
        Long categoryId,

        @NotBlank(message = "메뉴명은 필수입니다")
        @Size(max = 100, message = "메뉴명은 최대 100자입니다")
        String name,

        @NotNull(message = "가격은 필수입니다")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다")
        @Max(value = 10000000, message = "가격은 10,000,000 이하여야 합니다")
        Integer price,

        @Size(max = 500, message = "설명은 최대 500자입니다")
        String description
) {
}
