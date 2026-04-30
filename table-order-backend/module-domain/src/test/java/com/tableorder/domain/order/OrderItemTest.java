package com.tableorder.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    @DisplayName("OrderItem 생성 시 subtotal이 자동 계산된다")
    void create_calculatesSubtotal() {
        OrderItem item = OrderItem.builder()
                .orderId(1L)
                .menuId(10L)
                .menuName("카페라떼")
                .quantity(3)
                .unitPrice(5500)
                .build();

        assertThat(item.getSubtotal()).isEqualTo(16500); // 3 * 5500
    }

    @Test
    @DisplayName("수량이 1인 경우 subtotal은 unitPrice와 같다")
    void create_quantityOne_subtotalEqualsUnitPrice() {
        OrderItem item = OrderItem.builder()
                .orderId(1L)
                .menuId(5L)
                .menuName("아메리카노")
                .quantity(1)
                .unitPrice(4500)
                .build();

        assertThat(item.getSubtotal()).isEqualTo(4500);
    }

    @Test
    @DisplayName("OrderItem 필드가 올바르게 설정된다")
    void create_fieldsSetCorrectly() {
        OrderItem item = OrderItem.builder()
                .orderId(2L)
                .menuId(7L)
                .menuName("치즈케이크")
                .quantity(2)
                .unitPrice(7000)
                .build();

        assertThat(item.getOrderId()).isEqualTo(2L);
        assertThat(item.getMenuId()).isEqualTo(7L);
        assertThat(item.getMenuName()).isEqualTo("치즈케이크");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(7000);
    }
}
