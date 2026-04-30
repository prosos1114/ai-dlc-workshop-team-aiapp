package com.tableorder.admin.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TableCreateRequest(
        @NotNull(message = "테이블 번호는 필수입니다")
        @Min(value = 1, message = "테이블 번호는 1 이상이어야 합니다")
        Integer tableNumber,

        @NotNull(message = "비밀번호는 필수입니다")
        @Size(min = 4, message = "비밀번호는 최소 4자입니다")
        String password
) {
}
