package com.tableorder.customer.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TableLoginRequest(
        @NotBlank(message = "매장 코드는 필수입니다")
        String storeCode,

        @NotNull(message = "테이블 번호는 필수입니다")
        Integer tableNumber,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {
}
