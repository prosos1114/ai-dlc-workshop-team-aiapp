package com.tableorder.admin.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "매장 코드는 필수입니다")
        @Size(min = 3, max = 50, message = "매장 코드는 3~50자입니다")
        String storeCode,

        @NotBlank(message = "사용자명은 필수입니다")
        @Size(max = 50, message = "사용자명은 최대 50자입니다")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {
}
