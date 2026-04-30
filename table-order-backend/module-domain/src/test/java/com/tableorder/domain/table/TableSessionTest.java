package com.tableorder.domain.table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TableSessionTest {

    @Test
    @DisplayName("TableSession 생성 시 상태는 ACTIVE이다")
    void create_initialStatusIsActive() {
        TableSession session = TableSession.builder()
                .tableId(5L)
                .build();

        assertThat(session.getStatus()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(session.getTableId()).isEqualTo(5L);
        assertThat(session.getStartedAt()).isNotNull();
        assertThat(session.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("세션을 완료하면 상태가 COMPLETED로 변경된다")
    void complete_changesStatusToCompleted() {
        TableSession session = TableSession.builder()
                .tableId(5L)
                .build();

        session.complete();

        assertThat(session.getStatus()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(session.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("세션 완료 시 completedAt이 설정된다")
    void complete_setsCompletedAt() {
        TableSession session = TableSession.builder()
                .tableId(3L)
                .build();

        session.complete();

        assertThat(session.getCompletedAt()).isNotNull();
        assertThat(session.getCompletedAt()).isAfterOrEqualTo(session.getStartedAt());
    }
}
