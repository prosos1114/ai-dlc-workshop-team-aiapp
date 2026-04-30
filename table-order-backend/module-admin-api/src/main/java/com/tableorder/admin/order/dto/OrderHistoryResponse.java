package com.tableorder.admin.order.dto;

import com.tableorder.domain.order.OrderHistory;

import java.time.LocalDateTime;

public record OrderHistoryResponse(
        Long id,
        String orderNumber,
        int totalAmount,
        String items,
        LocalDateTime orderedAt,
        LocalDateTime completedAt
) {

    public static OrderHistoryResponse from(OrderHistory history) {
        return new OrderHistoryResponse(history.getId(), history.getOrderNumber(),
                history.getTotalAmount(), history.getItems(),
                history.getOrderedAt(), history.getCompletedAt());
    }
}
