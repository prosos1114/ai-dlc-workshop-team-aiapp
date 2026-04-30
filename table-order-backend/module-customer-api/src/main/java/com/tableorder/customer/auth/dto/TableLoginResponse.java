package com.tableorder.customer.auth.dto;

public record TableLoginResponse(
        String token,
        Long storeId,
        Long tableId,
        Integer tableNumber
) {
}
