package com.tableorder.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    Page<OrderHistory> findByTableIdOrderByCompletedAtDesc(Long tableId, Pageable pageable);

    Page<OrderHistory> findByTableIdAndCompletedAtBetweenOrderByCompletedAtDesc(
            Long tableId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
