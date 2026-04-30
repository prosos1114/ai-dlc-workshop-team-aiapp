package com.tableorder.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("Order 생성 시 상태는 PENDING이다")
    void create_initialStatusIsPending() {
        Order order = Order.builder()
                .storeId(1L)
                .tableId(5L)
                .sessionId(10L)
                .orderNumber("ORD-20260430-001")
                .totalAmount(25000)
                .build();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getStoreId()).isEqualTo(1L);
        assertThat(order.getTableId()).isEqualTo(5L);
        assertThat(order.getSessionId()).isEqualTo(10L);
        assertThat(order.getOrderNumber()).isEqualTo("ORD-20260430-001");
        assertThat(order.getTotalAmount()).isEqualTo(25000);
    }

    @Test
    @DisplayName("Order 상태를 변경할 수 있다")
    void updateStatus_changesStatus() {
        Order order = Order.builder()
                .storeId(1L)
                .tableId(5L)
                .sessionId(10L)
                .orderNumber("ORD-001")
                .totalAmount(10000)
                .build();

        order.updateStatus(OrderStatus.PREPARING);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    @DisplayName("Order에 아이템을 추가할 수 있다")
    void addItem_addsToList() {
        Order order = Order.builder()
                .storeId(1L)
                .tableId(5L)
                .sessionId(10L)
                .orderNumber("ORD-001")
                .totalAmount(30000)
                .build();

        OrderItem item = OrderItem.builder()
                .orderId(1L)
                .menuId(10L)
                .menuName("아메리카노")
                .quantity(2)
                .unitPrice(5000)
                .build();

        order.addItem(item);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getMenuName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("Order 생성 시 items 리스트는 비어있다")
    void create_itemsListIsEmpty() {
        Order order = Order.builder()
                .storeId(1L)
                .tableId(5L)
                .sessionId(10L)
                .orderNumber("ORD-001")
                .totalAmount(0)
                .build();

        assertThat(order.getItems()).isNotNull().isEmpty();
    }
}
