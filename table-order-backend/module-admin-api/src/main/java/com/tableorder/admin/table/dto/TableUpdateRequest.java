package com.tableorder.admin.table.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TableUpdateRequest(
        @NotNull(message = "비밀번호는 필수입니다")
        @Size(min = 4, message = "비밀번호는 최소 4자입니다")
        String password
) {
}
