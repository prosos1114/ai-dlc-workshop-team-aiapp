package com.tableorder.domain.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByStoreIdOrderByDisplayOrder(Long storeId);

    List<Menu> findByStoreIdAndCategoryIdOrderByDisplayOrder(Long storeId, Long categoryId);

    Optional<Menu> findByIdAndStoreId(Long id, Long storeId);

    int countByStoreIdAndCategoryId(Long storeId, Long categoryId);
}
