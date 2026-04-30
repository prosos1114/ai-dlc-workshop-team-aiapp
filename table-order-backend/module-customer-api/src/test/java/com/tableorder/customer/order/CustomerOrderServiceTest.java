package com.tableorder.customer.order;

import com.tableorder.core.exception.NotFoundException;
import com.tableorder.customer.order.dto.OrderCreateRequest;
import com.tableorder.customer.order.dto.OrderItemRequest;
import com.tableorder.customer.order.dto.OrderResponse;
import com.tableorder.domain.menu.Menu;
import com.tableorder.domain.menu.MenuRepository;
import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderRepository;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import com.tableorder.domain.table.SessionStatus;
import com.tableorder.domain.table.TableSession;
import com.tableorder.domain.table.TableSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private TableSessionRepository tableSessionRepository;

    private CustomerOrderService customerOrderService;

    @BeforeEach
    void setUp() {
        customerOrderService = new CustomerOrderService(orderRepository, menuRepository, storeRepository, tableSessionRepository);
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("활성 세션이 있으면 해당 세션에 주문을 생성한다")
        void createOrder_existingSession_success() {
            // given
            Long storeId = 1L;
            Long tableId = 5L;

            TableSession session = TableSession.builder().tableId(tableId).build();
            ReflectionTestUtils.setField(session, "id", 10L);

            Store store = Store.builder().name("카페").code("cafe-01").build();
            ReflectionTestUtils.setField(store, "id", storeId);

            Menu menu = Menu.builder().storeId(storeId).categoryId(1L).name("아메리카노").price(4500).displayOrder(1).build();
            ReflectionTestUtils.setField(menu, "id", 20L);

            OrderCreateRequest request = new OrderCreateRequest(
                    List.of(new OrderItemRequest(20L, 2))
            );

            when(tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE))
                    .thenReturn(Optional.of(session));
            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
            when(orderRepository.countByStoreIdAndCreatedAtBetween(eq(storeId), any(), any())).thenReturn(0L);
            when(menuRepository.findByIdAndStoreId(20L, storeId)).thenReturn(Optional.of(menu));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", 100L);
                return order;
            });

            // when
            OrderResponse response = customerOrderService.createOrder(storeId, tableId, request);

            // then
            assertThat(response.orderNumber()).startsWith("cafe-01-");
            assertThat(response.totalAmount()).isEqualTo(9000); // 4500 * 2
            assertThat(response.items()).hasSize(1);
            assertThat(response.items().get(0).menuName()).isEqualTo("아메리카노");
            assertThat(response.items().get(0).quantity()).isEqualTo(2);
            assertThat(response.items().get(0).subtotal()).isEqualTo(9000);
        }

        @Test
        @DisplayName("활성 세션이 없으면 새 세션을 생성한다")
        void createOrder_noSession_createsNewSession() {
            // given
            Long storeId = 1L;
            Long tableId = 5L;

            TableSession newSession = TableSession.builder().tableId(tableId).build();
            ReflectionTestUtils.setField(newSession, "id", 11L);

            Store store = Store.builder().name("카페").code("cafe-01").build();
            ReflectionTestUtils.setField(store, "id", storeId);

            Menu menu = Menu.builder().storeId(storeId).categoryId(1L).name("라떼").price(5500).displayOrder(1).build();
            ReflectionTestUtils.setField(menu, "id", 30L);

            OrderCreateRequest request = new OrderCreateRequest(
                    List.of(new OrderItemRequest(30L, 1))
            );

            when(tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE))
                    .thenReturn(Optional.empty());
            when(tableSessionRepository.save(any(TableSession.class))).thenReturn(newSession);
            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
            when(orderRepository.countByStoreIdAndCreatedAtBetween(eq(storeId), any(), any())).thenReturn(0L);
            when(menuRepository.findByIdAndStoreId(30L, storeId)).thenReturn(Optional.of(menu));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", 101L);
                return order;
            });

            // when
            OrderResponse response = customerOrderService.createOrder(storeId, tableId, request);

            // then
            assertThat(response.totalAmount()).isEqualTo(5500);
            verify(tableSessionRepository).save(any(TableSession.class));
        }

        @Test
        @DisplayName("존재하지 않는 메뉴로 주문 시 예외 발생")
        void createOrder_invalidMenu_throwsException() {
            // given
            Long storeId = 1L;
            Long tableId = 5L;

            TableSession session = TableSession.builder().tableId(tableId).build();
            ReflectionTestUtils.setField(session, "id", 10L);

            Store store = Store.builder().name("카페").code("cafe-01").build();
            ReflectionTestUtils.setField(store, "id", storeId);

            OrderCreateRequest request = new OrderCreateRequest(
                    List.of(new OrderItemRequest(999L, 1))
            );

            when(tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE))
                    .thenReturn(Optional.of(session));
            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
            when(orderRepository.countByStoreIdAndCreatedAtBetween(eq(storeId), any(), any())).thenReturn(0L);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", 102L);
                return order;
            });
            when(menuRepository.findByIdAndStoreId(999L, storeId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> customerOrderService.createOrder(storeId, tableId, request))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("여러 메뉴 항목으로 주문 시 총액이 올바르게 계산된다")
        void createOrder_multipleItems_correctTotal() {
            // given
            Long storeId = 1L;
            Long tableId = 5L;

            TableSession session = TableSession.builder().tableId(tableId).build();
            ReflectionTestUtils.setField(session, "id", 10L);

            Store store = Store.builder().name("카페").code("cafe-01").build();
            ReflectionTestUtils.setField(store, "id", storeId);

            Menu menu1 = Menu.builder().storeId(storeId).categoryId(1L).name("아메리카노").price(4500).displayOrder(1).build();
            ReflectionTestUtils.setField(menu1, "id", 20L);
            Menu menu2 = Menu.builder().storeId(storeId).categoryId(2L).name("치즈케이크").price(7000).displayOrder(1).build();
            ReflectionTestUtils.setField(menu2, "id", 21L);

            OrderCreateRequest request = new OrderCreateRequest(
                    List.of(new OrderItemRequest(20L, 2), new OrderItemRequest(21L, 1))
            );

            when(tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE))
                    .thenReturn(Optional.of(session));
            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
            when(orderRepository.countByStoreIdAndCreatedAtBetween(eq(storeId), any(), any())).thenReturn(0L);
            when(menuRepository.findByIdAndStoreId(20L, storeId)).thenReturn(Optional.of(menu1));
            when(menuRepository.findByIdAndStoreId(21L, storeId)).thenReturn(Optional.of(menu2));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                ReflectionTestUtils.setField(order, "id", 103L);
                return order;
            });

            // when
            OrderResponse response = customerOrderService.createOrder(storeId, tableId, request);

            // then
            assertThat(response.totalAmount()).isEqualTo(16000); // (4500*2) + (7000*1)
            assertThat(response.items()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("세션 주문 조회")
    class GetSessionOrders {

        @Test
        @DisplayName("활성 세션의 주문 목록을 조회한다")
        void getSessionOrders_withActiveSession_returnsOrders() {
            Long storeId = 1L;
            Long tableId = 5L;

            TableSession session = TableSession.builder().tableId(tableId).build();
            ReflectionTestUtils.setField(session, "id", 10L);

            Order order = Order.builder()
                    .storeId(storeId).tableId(tableId).sessionId(10L)
                    .orderNumber("cafe-01-20260430-0001").totalAmount(9000)
                    .build();
            ReflectionTestUtils.setField(order, "id", 100L);

            when(tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE))
                    .thenReturn(Optional.of(session));
            when(orderRepository.findByStoreIdAndSessionIdOrderByCreatedAtAsc(storeId, 10L))
                    .thenReturn(List.of(order));

            List<OrderResponse> result = customerOrderService.getSessionOrders(storeId, tableId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).orderNumber()).isEqualTo("cafe-01-20260430-0001");
        }

        @Test
        @DisplayName("활성 세션이 없으면 빈 리스트를 반환한다")
        void getSessionOrders_noActiveSession_returnsEmpty() {
            Long storeId = 1L;
            Long tableId = 5L;

            when(tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            List<OrderResponse> result = customerOrderService.getSessionOrders(storeId, tableId);

            assertThat(result).isEmpty();
        }
    }
}
