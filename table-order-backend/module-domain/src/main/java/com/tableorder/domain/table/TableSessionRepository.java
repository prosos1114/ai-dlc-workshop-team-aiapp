package com.tableorder.domain.table;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableSessionRepository extends JpaRepository<TableSession, Long> {

    Optional<TableSession> findByTableIdAndStatus(Long tableId, SessionStatus status);
}
