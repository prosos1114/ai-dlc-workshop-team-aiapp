package com.tableorder.admin.order;

import com.tableorder.admin.order.dto.OrderHistoryResponse;
import com.tableorder.admin.order.dto.OrderResponse;
import com.tableorder.admin.sse.SSEService;
import com.tableorder.admin.sse.dto.OrderEventData;
import com.tableorder.core.exception.InvalidStatusTransitionException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderHistory;
import com.tableorder.domain.order.OrderHistoryRepository;
import com.tableorder.domain.order.OrderRepository;
import com.tableorder.domain.order.OrderStatus;
import com.tableorder.domain.table.TableEntity;
import com.tableorder.domain.table.TableRepository;
import com.tableorder.core.dto.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderManageService {

    private static final Logger log = LoggerFactory.getLogger(OrderManageService.class);

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final TableRepository tableRepository;
    private final SSEService sseService;

    public OrderManageService(OrderRepository orderRepository,
                              OrderHistoryRepository orderHistoryRepository,
                              TableRepository tableRepository,
                              SSEService sseService) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.tableRepository = tableRepository;
        this.sseService = sseService;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(Long storeId, String status) {
        Map<Long, Integer> tableNumberMap = getTableNumberMap(storeId);

        List<Order> orders;
        if (status != null) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            orders = orderRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(storeId, orderStatus);
        } else {
            orders = orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
        }

        return orders.stream()
                .map(order -> OrderResponse.from(order, tableNumberMap.get(order.getTableId())))
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long storeId, Long orderId, String newStatusStr) {
        Order order = findOrderByIdAndStore(orderId, storeId);
        OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(
                    order.getStatus().name(), newStatus.name());
        }

        order.updateStatus(newStatus);
        orderRepository.save(order);

        Integer tableNumber = getTableNumber(order.getTableId());
        sseService.publish(storeId, OrderEventData.orderStatusChanged(
                order.getId(), order.getOrderNumber(), tableNumber,
                newStatus.name(), order.getTotalAmount()));

        log.info("Order status updated: orderId={}, {} -> {}",
                orderId, order.getStatus(), newStatus);
        return OrderResponse.from(order, tableNumber);
    }

    @Transactional
    public void deleteOrder(Long storeId, Long orderId) {
        Order order = findOrderByIdAndStore(orderId, storeId);
        Integer tableNumber = getTableNumber(order.getTableId());

        orderRepository.delete(order);

        sseService.publish(storeId, OrderEventData.orderDeleted(
                order.getId(), order.getOrderNumber(), tableNumber));

        log.info("Order deleted: orderId={}", orderId);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderHistoryResponse> getOrderHistory(Long storeId, Long tableId,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate,
                                                               int page, int size) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table", tableId));
        if (!table.getStoreId().equals(storeId)) {
            throw new NotFoundException("Table", tableId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderHistory> historyPage;

        if (startDate != null && endDate != null) {
            historyPage = orderHistoryRepository
                    .findByTableIdAndCompletedAtBetweenOrderByCompletedAtDesc(
                            tableId, startDate, endDate, pageable);
        } else {
            historyPage = orderHistoryRepository
                    .findByTableIdOrderByCompletedAtDesc(tableId, pageable);
        }

        Page<OrderHistoryResponse> responsePage = historyPage.map(OrderHistoryResponse::from);
        return PageResponse.from(responsePage);
    }

    private Order findOrderByIdAndStore(Long orderId, Long storeId) {
        return orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));
    }

    private Map<Long, Integer> getTableNumberMap(Long storeId) {
        return tableRepository.findByStoreId(storeId).stream()
                .collect(Collectors.toMap(TableEntity::getId, TableEntity::getTableNumber));
    }

    private Integer getTableNumber(Long tableId) {
        return tableRepository.findById(tableId)
                .map(TableEntity::getTableNumber)
                .orElse(null);
    }
}
