package com.tableorder.admin.table;

import com.tableorder.admin.sse.SSEService;
import com.tableorder.admin.table.dto.TableResponse;
import com.tableorder.core.exception.DuplicateResourceException;
import com.tableorder.core.exception.NoActiveSessionException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.order.Order;
import com.tableorder.domain.order.OrderHistoryRepository;
import com.tableorder.domain.order.OrderRepository;
import com.tableorder.domain.table.SessionStatus;
import com.tableorder.domain.table.TableEntity;
import com.tableorder.domain.table.TableRepository;
import com.tableorder.domain.table.TableSession;
import com.tableorder.domain.table.TableSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TableManageServiceTest {

    @Mock private TableRepository tableRepository;
    @Mock private TableSessionRepository tableSessionRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderHistoryRepository orderHistoryRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SSEService sseService;

    @InjectMocks private TableManageService tableManageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tableManageService, "objectMapper", new ObjectMapper());
    }

    @Test
    @DisplayName("테이블 생성 성공")
    void createTable_success() {
        given(tableRepository.existsByStoreIdAndTableNumber(1L, 1)).willReturn(false);
        given(passwordEncoder.encode("1234")).willReturn("encoded");

        TableResponse result = tableManageService.createTable(1L, 1, "1234");

        assertThat(result.tableNumber()).isEqualTo(1);
        assertThat(result.hasActiveSession()).isFalse();
        verify(tableRepository).save(any(TableEntity.class));
    }

    @Test
    @DisplayName("중복 테이블 번호 생성 시 DuplicateResourceException")
    void createTable_duplicate() {
        given(tableRepository.existsByStoreIdAndTableNumber(1L, 1)).willReturn(true);

        assertThatThrownBy(() -> tableManageService.createTable(1L, 1, "1234"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("이용 완료 - ACTIVE 세션이 없으면 NoActiveSessionException")
    void completeTable_noActiveSession() {
        TableEntity table = TableEntity.builder().storeId(1L).tableNumber(1).password("enc").build();
        ReflectionTestUtils.setField(table, "id", 1L);
        given(tableRepository.findById(1L)).willReturn(Optional.of(table));
        given(tableSessionRepository.findByTableIdAndStatus(1L, SessionStatus.ACTIVE))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> tableManageService.completeTable(1L, 1L))
                .isInstanceOf(NoActiveSessionException.class);
    }

    @Test
    @DisplayName("이용 완료 성공 - 주문이 이력으로 이동된다")
    void completeTable_success() {
        TableEntity table = TableEntity.builder().storeId(1L).tableNumber(1).password("enc").build();
        ReflectionTestUtils.setField(table, "id", 1L);

        TableSession session = new TableSession(1L);
        ReflectionTestUtils.setField(session, "id", 10L);

        Order order = Order.builder()
                .storeId(1L).tableId(1L).sessionId(10L)
                .orderNumber("test-001").totalAmount(10000).build();
        ReflectionTestUtils.setField(order, "id", 100L);

        given(tableRepository.findById(1L)).willReturn(Optional.of(table));
        given(tableSessionRepository.findByTableIdAndStatus(1L, SessionStatus.ACTIVE))
                .willReturn(Optional.of(session));
        given(orderRepository.findBySessionId(10L)).willReturn(List.of(order));

        tableManageService.completeTable(1L, 1L);

        verify(orderHistoryRepository).save(any());
        verify(orderRepository).deleteAll(any());
        verify(tableSessionRepository).save(session);
        verify(sseService).publish(anyLong(), any());
    }
}
