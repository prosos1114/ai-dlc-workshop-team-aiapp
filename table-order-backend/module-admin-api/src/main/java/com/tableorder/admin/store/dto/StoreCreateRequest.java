package com.tableorder.admin.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StoreCreateRequest(
        @NotBlank(message = "매장명은 필수입니다")
        @Size(max = 100, message = "매장명은 최대 100자입니다")
        String name,

        @NotBlank(message = "매장 코드는 필수입니다")
        @Size(min = 3, max = 50, message = "매장 코드는 3~50자입니다")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "매장 코드는 영문 소문자, 숫자, 하이픈만 허용됩니다")
        String code
) {
}
