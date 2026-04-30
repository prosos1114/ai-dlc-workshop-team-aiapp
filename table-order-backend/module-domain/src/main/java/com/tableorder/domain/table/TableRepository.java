package com.tableorder.domain.table;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<TableEntity, Long> {

    List<TableEntity> findByStoreId(Long storeId);

    Optional<TableEntity> findByStoreIdAndTableNumber(Long storeId, Integer tableNumber);

    boolean existsByStoreIdAndTableNumber(Long storeId, Integer tableNumber);
}
