package com.tableorder.domain.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "table_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tableId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder
    public TableSession(Long tableId) {
        this.tableId = tableId;
        this.status = SessionStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = SessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
