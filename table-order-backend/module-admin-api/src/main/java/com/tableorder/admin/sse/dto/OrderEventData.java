package com.tableorder.admin.sse.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderEventData(
        String type,
        Long orderId,
        String orderNumber,
        Integer tableNumber,
        String status,
        Integer totalAmount,
        List<OrderItemSummary> items,
        LocalDateTime timestamp
) {

    public record OrderItemSummary(String menuName, int quantity) {
    }

    public static OrderEventData orderStatusChanged(Long orderId, String orderNumber,
                                                     Integer tableNumber, String status,
                                                     Integer totalAmount) {
        return new OrderEventData("ORDER_STATUS_CHANGED", orderId, orderNumber,
                tableNumber, status, totalAmount, null, LocalDateTime.now());
    }

    public static OrderEventData orderDeleted(Long orderId, String orderNumber,
                                               Integer tableNumber) {
        return new OrderEventData("ORDER_DELETED", orderId, orderNumber,
                tableNumber, null, null, null, LocalDateTime.now());
    }

    public static OrderEventData tableCompleted(Integer tableNumber) {
        return new OrderEventData("TABLE_COMPLETED", null, null,
                tableNumber, null, null, null, LocalDateTime.now());
    }
}
