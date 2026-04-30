package com.tableorder.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Long tableId;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String items;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Builder
    public OrderHistory(Long storeId, Long tableId, Long sessionId, String orderNumber,
                        int totalAmount, String items, LocalDateTime orderedAt, LocalDateTime completedAt) {
        this.storeId = storeId;
        this.tableId = tableId;
        this.sessionId = sessionId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.items = items;
        this.orderedAt = orderedAt;
        this.completedAt = completedAt;
    }
}
