package com.tableorder.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStoreIdAndSessionIdOrderByCreatedAtAsc(Long storeId, Long sessionId);

    List<Order> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    List<Order> findByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, OrderStatus status);

    List<Order> findBySessionId(Long sessionId);

    Optional<Order> findByIdAndStoreId(Long id, Long storeId);

    long countByStoreIdAndCreatedAtBetween(Long storeId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
