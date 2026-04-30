package com.tableorder.admin.store;

import com.tableorder.admin.store.dto.StoreResponse;
import com.tableorder.core.exception.DuplicateResourceException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock private StoreRepository storeRepository;
    @InjectMocks private StoreService storeService;

    @Test
    @DisplayName("매장 등록 성공")
    void createStore_success() {
        given(storeRepository.existsByCode("new-store")).willReturn(false);

        StoreResponse result = storeService.createStore("새매장", "new-store");

        assertThat(result.name()).isEqualTo("새매장");
        assertThat(result.code()).isEqualTo("new-store");
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    @DisplayName("중복 매장 코드로 등록 시 DuplicateResourceException")
    void createStore_duplicateCode() {
        given(storeRepository.existsByCode("existing")).willReturn(true);

        assertThatThrownBy(() -> storeService.createStore("매장", "existing"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("매장 코드로 조회 성공")
    void getStoreByCode_success() {
        Store store = Store.builder().name("테스트").code("test").build();
        ReflectionTestUtils.setField(store, "id", 1L);
        given(storeRepository.findByCode("test")).willReturn(Optional.of(store));

        StoreResponse result = storeService.getStoreByCode("test");

        assertThat(result.code()).isEqualTo("test");
    }

    @Test
    @DisplayName("존재하지 않는 매장 코드 조회 시 NotFoundException")
    void getStoreByCode_notFound() {
        given(storeRepository.findByCode("invalid")).willReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.getStoreByCode("invalid"))
                .isInstanceOf(NotFoundException.class);
    }
}
