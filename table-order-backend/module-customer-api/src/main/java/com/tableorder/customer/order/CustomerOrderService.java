package com.tableorder.customer.order;

import com.tableorder.core.exception.NotFoundException;
import com.tableorder.customer.order.dto.OrderCreateRequest;
import com.tableorder.customer.order.dto.OrderItemRequest;
import com.tableorder.customer.order.dto.OrderResponse;
import com.tableorder.domain.menu.Menu;
import com.tableorder.domain.menu.MenuRepository;
import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderItem;
import com.tableorder.domain.order.OrderRepository;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import com.tableorder.domain.table.SessionStatus;
import com.tableorder.domain.table.TableSession;
import com.tableorder.domain.table.TableSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CustomerOrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final TableSessionRepository tableSessionRepository;

    public CustomerOrderService(OrderRepository orderRepository,
                                MenuRepository menuRepository,
                                StoreRepository storeRepository,
                                TableSessionRepository tableSessionRepository) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.storeRepository = storeRepository;
        this.tableSessionRepository = tableSessionRepository;
    }

    @Transactional
    public OrderResponse createOrder(Long storeId, Long tableId, OrderCreateRequest request) {
        // 활성 세션 조회 또는 생성
        TableSession session = tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE)
                .orElseGet(() -> tableSessionRepository.save(TableSession.builder().tableId(tableId).build()));

        // 주문 번호 생성
        String orderNumber = generateOrderNumber(storeId);

        // 주문 항목 처리 및 총액 계산
        int totalAmount = 0;
        Order order = Order.builder()
                .storeId(storeId)
                .tableId(tableId)
                .sessionId(session.getId())
                .orderNumber(orderNumber)
                .totalAmount(0)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (OrderItemRequest itemRequest : request.items()) {
            Menu menu = menuRepository.findByIdAndStoreId(itemRequest.menuId(), storeId)
                    .orElseThrow(() -> new NotFoundException("메뉴", itemRequest.menuId()));

            OrderItem orderItem = OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .menuId(menu.getId())
                    .menuName(menu.getName())
                    .quantity(itemRequest.quantity())
                    .unitPrice(menu.getPrice())
                    .build();

            savedOrder.addItem(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        savedOrder.updateTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getSessionOrders(Long storeId, Long tableId) {
        TableSession session = tableSessionRepository.findByTableIdAndStatus(tableId, SessionStatus.ACTIVE)
                .orElse(null);

        if (session == null) {
            return List.of();
        }

        List<Order> orders = orderRepository.findByStoreIdAndSessionIdOrderByCreatedAtAsc(storeId, session.getId());
        return orders.stream().map(OrderResponse::from).toList();
    }

    private String generateOrderNumber(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("매장", storeId));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long count = orderRepository.countByStoreIdAndCreatedAtBetween(storeId, startOfDay, endOfDay);
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return String.format("%s-%s-%04d", store.getCode(), dateStr, count + 1);
    }
}
