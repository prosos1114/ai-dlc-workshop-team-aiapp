package com.tableorder.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    @DisplayName("PENDING에서 PREPARING으로 전환 가능하다")
    void pending_canTransitionTo_preparing() {
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.PREPARING)).isTrue();
    }

    @Test
    @DisplayName("PENDING에서 COMPLETED로 직접 전환 불가하다")
    void pending_cannotTransitionTo_completed() {
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.COMPLETED)).isFalse();
    }

    @Test
    @DisplayName("PREPARING에서 COMPLETED로 전환 가능하다")
    void preparing_canTransitionTo_completed() {
        assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.COMPLETED)).isTrue();
    }

    @Test
    @DisplayName("PREPARING에서 PENDING으로 역전환 불가하다")
    void preparing_cannotTransitionTo_pending() {
        assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.PENDING)).isFalse();
    }

    @Test
    @DisplayName("COMPLETED에서 어떤 상태로도 전환 불가하다")
    void completed_cannotTransitionToAny() {
        assertThat(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.PENDING)).isFalse();
        assertThat(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.PREPARING)).isFalse();
        assertThat(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.COMPLETED)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, PENDING, false",
            "PENDING, PREPARING, true",
            "PENDING, COMPLETED, false",
            "PREPARING, PENDING, false",
            "PREPARING, PREPARING, false",
            "PREPARING, COMPLETED, true",
            "COMPLETED, PENDING, false",
            "COMPLETED, PREPARING, false",
            "COMPLETED, COMPLETED, false"
    })
    @DisplayName("모든 상태 전환 조합을 검증한다")
    void canTransitionTo_allCombinations(OrderStatus from, OrderStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }
}
