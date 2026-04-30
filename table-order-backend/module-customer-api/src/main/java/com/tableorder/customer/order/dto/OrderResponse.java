package com.tableorder.customer.order.dto;

import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String status,
        int totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getTotalAmount(),
                itemResponses,
                order.getCreatedAt()
        );
    }

    public record OrderItemResponse(
            Long id,
            Long menuId,
            String menuName,
            int quantity,
            int unitPrice,
            int subtotal
    ) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getId(),
                    item.getMenuId(),
                    item.getMenuName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getSubtotal()
            );
        }
    }
}
