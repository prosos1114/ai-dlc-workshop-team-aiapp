package com.tableorder.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByStoreIdAndUsername(Long storeId, String username);

    boolean existsByStoreIdAndUsername(Long storeId, String username);
}
