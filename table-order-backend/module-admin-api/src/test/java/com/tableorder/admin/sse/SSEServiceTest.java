package com.tableorder.admin.sse;

import com.tableorder.admin.sse.dto.OrderEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SSEServiceTest {

    private SSEService sseService;

    @BeforeEach
    void setUp() {
        sseService = new SSEService();
    }

    @Test
    @DisplayName("SSE 구독 시 SseEmitter를 반환한다")
    void subscribe_returnsEmitter() {
        SseEmitter emitter = sseService.subscribe(1L);

        assertThat(emitter).isNotNull();
    }

    @Test
    @DisplayName("구독자가 없는 매장에 이벤트 발행 시 에러 없이 처리된다")
    void publish_noSubscribers() {
        OrderEventData event = OrderEventData.orderStatusChanged(
                1L, "test-001", 1, "PREPARING", 10000);

        assertThatCode(() -> sseService.publish(99L, event))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("하트비트 실행 시 에러 없이 처리된다")
    void heartbeat_noError() {
        sseService.subscribe(1L);

        assertThatCode(() -> sseService.heartbeat())
                .doesNotThrowAnyException();
    }
}
