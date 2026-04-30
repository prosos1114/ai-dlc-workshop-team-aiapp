package com.tableorder.admin.order;

import com.tableorder.admin.order.dto.OrderResponse;
import com.tableorder.admin.sse.SSEService;
import com.tableorder.core.exception.InvalidStatusTransitionException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderHistoryRepository;
import com.tableorder.domain.order.OrderRepository;
import com.tableorder.domain.order.OrderStatus;
import com.tableorder.domain.table.TableEntity;
import com.tableorder.domain.table.TableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderManageServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderHistoryRepository orderHistoryRepository;
    @Mock private TableRepository tableRepository;
    @Mock private SSEService sseService;

    @InjectMocks private OrderManageService orderManageService;

    @Test
    @DisplayName("주문 상태 변경 성공 - PENDING → PREPARING")
    void updateOrderStatus_success() {
        Order order = Order.builder()
                .storeId(1L).tableId(1L).sessionId(1L)
                .orderNumber("test-001").totalAmount(10000).build();
        ReflectionTestUtils.setField(order, "id", 1L);

        TableEntity table = TableEntity.builder().storeId(1L).tableNumber(1).password("enc").build();
        ReflectionTestUtils.setField(table, "id", 1L);

        given(orderRepository.findByIdAndStoreId(1L, 1L)).willReturn(Optional.of(order));
        given(tableRepository.findById(1L)).willReturn(Optional.of(table));

        OrderResponse result = orderManageService.updateOrderStatus(1L, 1L, "PREPARING");

        assertThat(result.status()).isEqualTo("PREPARING");
        verify(orderRepository).save(order);
        verify(sseService).publish(anyLong(), any());
    }

    @Test
    @DisplayName("잘못된 상태 전이 시 InvalidStatusTransitionException")
    void updateOrderStatus_invalidTransition() {
        Order order = Order.builder()
                .storeId(1L).tableId(1L).sessionId(1L)
                .orderNumber("test-001").totalAmount(10000).build();
        ReflectionTestUtils.setField(order, "id", 1L);
        // PENDING → COMPLETED 직접 전이 불가
        given(orderRepository.findByIdAndStoreId(1L, 1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderManageService.updateOrderStatus(1L, 1L, "COMPLETED"))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    @DisplayName("주문 삭제 성공")
    void deleteOrder_success() {
        Order order = Order.builder()
                .storeId(1L).tableId(1L).sessionId(1L)
                .orderNumber("test-001").totalAmount(10000).build();
        ReflectionTestUtils.setField(order, "id", 1L);

        TableEntity table = TableEntity.builder().storeId(1L).tableNumber(1).password("enc").build();
        ReflectionTestUtils.setField(table, "id", 1L);

        given(orderRepository.findByIdAndStoreId(1L, 1L)).willReturn(Optional.of(order));
        given(tableRepository.findById(1L)).willReturn(Optional.of(table));

        orderManageService.deleteOrder(1L, 1L);

        verify(orderRepository).delete(order);
        verify(sseService).publish(anyLong(), any());
    }

    @Test
    @DisplayName("존재하지 않는 주문 상태 변경 시 NotFoundException")
    void updateOrderStatus_notFound() {
        given(orderRepository.findByIdAndStoreId(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderManageService.updateOrderStatus(1L, 99L, "PREPARING"))
                .isInstanceOf(NotFoundException.class);
    }
}
