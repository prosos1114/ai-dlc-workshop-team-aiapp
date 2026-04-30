package com.tableorder.admin.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrderStatusUpdateRequest(
        @NotBlank(message = "상태는 필수입니다")
        @Pattern(regexp = "^(PREPARING|COMPLETED)$", message = "상태는 PREPARING 또는 COMPLETED만 허용됩니다")
        String status
) {
}
