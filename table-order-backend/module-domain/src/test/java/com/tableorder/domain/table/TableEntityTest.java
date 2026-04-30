package com.tableorder.domain.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TableEntityTest {

    @Test
    @DisplayName("TableEntity 생성 시 필드가 올바르게 설정된다")
    void create_fieldsSetCorrectly() {
        TableEntity table = TableEntity.builder()
                .storeId(1L)
                .tableNumber(5)
                .password("table-password")
                .build();

        assertThat(table.getStoreId()).isEqualTo(1L);
        assertThat(table.getTableNumber()).isEqualTo(5);
        assertThat(table.getPassword()).isEqualTo("table-password");
    }

    @Test
    @DisplayName("비밀번호를 변경할 수 있다")
    void updatePassword_changesPassword() {
        TableEntity table = TableEntity.builder()
                .storeId(1L)
                .tableNumber(3)
                .password("old-password")
                .build();

        table.updatePassword("new-password");

        assertThat(table.getPassword()).isEqualTo("new-password");
    }
}
