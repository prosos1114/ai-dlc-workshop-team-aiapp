package com.tableorder.admin.table;

import com.tableorder.admin.sse.SSEService;
import com.tableorder.admin.sse.dto.OrderEventData;
import com.tableorder.admin.table.dto.TableResponse;
import com.tableorder.core.exception.DuplicateResourceException;
import com.tableorder.core.exception.NoActiveSessionException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderHistory;
import com.tableorder.domain.order.OrderHistoryRepository;
import com.tableorder.domain.order.OrderItem;
import com.tableorder.domain.order.OrderRepository;
import com.tableorder.domain.table.SessionStatus;
import com.tableorder.domain.table.TableEntity;
import com.tableorder.domain.table.TableRepository;
import com.tableorder.domain.table.TableSession;
import com.tableorder.domain.table.TableSessionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TableManageService {

    private static final Logger log = LoggerFactory.getLogger(TableManageService.class);

    private final TableRepository tableRepository;
    private final TableSessionRepository tableSessionRepository;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final SSEService sseService;
    private final ObjectMapper objectMapper;

    public TableManageService(TableRepository tableRepository,
                              TableSessionRepository tableSessionRepository,
                              OrderRepository orderRepository,
                              OrderHistoryRepository orderHistoryRepository,
                              PasswordEncoder passwordEncoder,
                              SSEService sseService,
                              ObjectMapper objectMapper) {
        this.tableRepository = tableRepository;
        this.tableSessionRepository = tableSessionRepository;
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.sseService = sseService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TableResponse createTable(Long storeId, Integer tableNumber, String password) {
        if (tableRepository.existsByStoreIdAndTableNumber(storeId, tableNumber)) {
            throw new DuplicateResourceException("Table", tableNumber);
        }

        TableEntity table = TableEntity.builder()
                .storeId(storeId)
                .tableNumber(tableNumber)
                .password(passwordEncoder.encode(password))
                .build();

        tableRepository.save(table);
        log.info("Table created: storeId={}, tableNumber={}", storeId, tableNumber);
        return TableResponse.from(table, false, 0, 0);
    }

    @Transactional(readOnly = true)
    public List<TableResponse> getTablesByStore(Long storeId) {
        List<TableEntity> tables = tableRepository.findByStoreId(storeId);
        return tables.stream().map(table -> {
            var sessionOpt = tableSessionRepository.findByTableIdAndStatus(
                    table.getId(), SessionStatus.ACTIVE);
            if (sessionOpt.isPresent()) {
                List<Order> orders = orderRepository.findBySessionId(sessionOpt.get().getId());
                int orderCount = orders.size();
                int totalAmount = orders.stream().mapToInt(Order::getTotalAmount).sum();
                return TableResponse.from(table, true, orderCount, totalAmount);
            }
            return TableResponse.from(table, false, 0, 0);
        }).toList();
    }

    @Transactional
    public TableResponse updateTable(Long storeId, Long tableId, String password) {
        TableEntity table = findTableByIdAndStore(tableId, storeId);
        table.updatePassword(passwordEncoder.encode(password));
        tableRepository.save(table);
        log.info("Table updated: tableId={}", tableId);

        var sessionOpt = tableSessionRepository.findByTableIdAndStatus(
                table.getId(), SessionStatus.ACTIVE);
        if (sessionOpt.isPresent()) {
            List<Order> orders = orderRepository.findBySessionId(sessionOpt.get().getId());
            return TableResponse.from(table, true, orders.size(),
                    orders.stream().mapToInt(Order::getTotalAmount).sum());
        }
        return TableResponse.from(table, false, 0, 0);
    }

    @Transactional
    public void completeTable(Long storeId, Long tableId) {
        TableEntity table = findTableByIdAndStore(tableId, storeId);

        TableSession session = tableSessionRepository
                .findByTableIdAndStatus(tableId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new NoActiveSessionException(tableId));

        List<Order> orders = orderRepository.findBySessionId(session.getId());

        for (Order order : orders) {
            String itemsJson = serializeItems(order.getItems());
            OrderHistory history = OrderHistory.builder()
                    .storeId(order.getStoreId())
                    .tableId(order.getTableId())
                    .sessionId(order.getSessionId())
                    .orderNumber(order.getOrderNumber())
                    .totalAmount(order.getTotalAmount())
                    .items(itemsJson)
                    .orderedAt(order.getCreatedAt())
                    .completedAt(LocalDateTime.now())
                    .build();
            orderHistoryRepository.save(history);
        }

        orderRepository.deleteAll(orders);
        session.complete();
        tableSessionRepository.save(session);

        sseService.publish(storeId, OrderEventData.tableCompleted(table.getTableNumber()));
        log.info("Table completed: storeId={}, tableId={}, ordersArchived={}",
                storeId, tableId, orders.size());
    }

    private TableEntity findTableByIdAndStore(Long tableId, Long storeId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table", tableId));
        if (!table.getStoreId().equals(storeId)) {
            throw new NotFoundException("Table", tableId);
        }
        return table;
    }

    private String serializeItems(List<OrderItem> items) {
        try {
            return objectMapper.writeValueAsString(
                    items.stream().map(item -> new ItemSnapshot(
                            item.getMenuName(), item.getQuantity(),
                            item.getUnitPrice(), item.getSubtotal()
                    )).toList());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order items", e);
            return "[]";
        }
    }

    private record ItemSnapshot(String menuName, int quantity, int unitPrice, int subtotal) {
    }
}
