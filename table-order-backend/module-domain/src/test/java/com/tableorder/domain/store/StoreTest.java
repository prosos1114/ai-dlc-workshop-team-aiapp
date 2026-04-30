package com.tableorder.domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoreTest {

    @Test
    @DisplayName("Store 생성 시 필드가 올바르게 설정된다")
    void create_fieldsSetCorrectly() {
        Store store = Store.builder()
                .name("카페 테스트")
                .code("CAFE-001")
                .build();

        assertThat(store.getName()).isEqualTo("카페 테스트");
        assertThat(store.getCode()).isEqualTo("CAFE-001");
    }

    @Test
    @DisplayName("Store는 name과 code를 가진다")
    void create_hasNameAndCode() {
        Store store = Store.builder()
                .name("맛있는 식당")
                .code("REST-ABC")
                .build();

        assertThat(store.getName()).isNotNull();
        assertThat(store.getCode()).isNotNull();
    }
}
