package com.tableorder.admin.store;

import com.tableorder.admin.store.dto.StoreResponse;
import com.tableorder.core.exception.DuplicateResourceException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional
    public StoreResponse createStore(String name, String code) {
        if (storeRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Store", code);
        }

        Store store = Store.builder().name(name).code(code).build();
        storeRepository.save(store);
        log.info("Store created: name={}, code={}", name, code);
        return StoreResponse.from(store);
    }

    @Transactional(readOnly = true)
    public StoreResponse getStoreByCode(String storeCode) {
        Store store = storeRepository.findByCode(storeCode)
                .orElseThrow(() -> new NotFoundException("Store", storeCode));
        return StoreResponse.from(store);
    }
}
