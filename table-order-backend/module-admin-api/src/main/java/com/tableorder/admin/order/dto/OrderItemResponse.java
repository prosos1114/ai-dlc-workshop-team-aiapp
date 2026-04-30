package com.tableorder.admin.order.dto;

import com.tableorder.domain.order.OrderItem;

public record OrderItemResponse(Long id, String menuName, int quantity, int unitPrice, int subtotal) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getMenuName(),
                item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
    }
}
