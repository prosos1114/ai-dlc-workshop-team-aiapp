package com.tableorder.admin.table.dto;

import com.tableorder.domain.table.TableEntity;

public record TableResponse(
        Long id,
        Long storeId,
        Integer tableNumber,
        boolean hasActiveSession,
        int currentSessionOrderCount,
        int currentSessionTotalAmount
) {

    public static TableResponse from(TableEntity table, boolean hasActiveSession,
                                      int orderCount, int totalAmount) {
        return new TableResponse(table.getId(), table.getStoreId(),
                table.getTableNumber(), hasActiveSession, orderCount, totalAmount);
    }
}
