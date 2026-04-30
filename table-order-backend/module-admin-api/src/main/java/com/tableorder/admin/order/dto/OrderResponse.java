package com.tableorder.admin.order.dto;

import com.tableorder.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long tableId,
        Integer tableNumber,
        String status,
        int totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {

    public static OrderResponse from(Order order, Integer tableNumber) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(order.getId(), order.getOrderNumber(),
                order.getTableId(), tableNumber, order.getStatus().name(),
                order.getTotalAmount(), itemResponses, order.getCreatedAt());
    }
}
