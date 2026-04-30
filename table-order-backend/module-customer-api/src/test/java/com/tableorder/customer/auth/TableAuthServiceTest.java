package com.tableorder.customer.auth;

import com.tableorder.core.exception.InvalidCredentialsException;
import com.tableorder.core.security.JwtTokenProvider;
import com.tableorder.customer.auth.dto.TableLoginRequest;
import com.tableorder.customer.auth.dto.TableLoginResponse;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import com.tableorder.domain.table.TableEntity;
import com.tableorder.domain.table.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableAuthServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private TableRepository tableRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TableAuthService tableAuthService;

    @BeforeEach
    void setUp() {
        tableAuthService = new TableAuthService(storeRepository, tableRepository, jwtTokenProvider, passwordEncoder);
    }

    @Test
    @DisplayName("유효한 인증 정보로 로그인 성공")
    void login_validCredentials_success() {
        // given
        TableLoginRequest request = new TableLoginRequest("cafe-01", 1, "1234");

        Store store = Store.builder().name("카페01").code("cafe-01").build();
        ReflectionTestUtils.setField(store, "id", 100L);

        TableEntity table = TableEntity.builder().storeId(100L).tableNumber(1).password("encoded-pw").build();
        ReflectionTestUtils.setField(table, "id", 5L);

        when(storeRepository.findByCode("cafe-01")).thenReturn(Optional.of(store));
        when(tableRepository.findByStoreIdAndTableNumber(100L, 1)).thenReturn(Optional.of(table));
        when(passwordEncoder.matches("1234", "encoded-pw")).thenReturn(true);
        when(jwtTokenProvider.createTableToken(5L, 100L)).thenReturn("jwt-token");

        // when
        TableLoginResponse response = tableAuthService.login(request);

        // then
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.storeId()).isEqualTo(100L);
        assertThat(response.tableId()).isEqualTo(5L);
        assertThat(response.tableNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 매장 코드로 로그인 시 예외 발생")
    void login_invalidStoreCode_throwsException() {
        TableLoginRequest request = new TableLoginRequest("invalid-code", 1, "1234");

        when(storeRepository.findByCode("invalid-code")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableAuthService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("존재하지 않는 테이블 번호로 로그인 시 예외 발생")
    void login_invalidTableNumber_throwsException() {
        TableLoginRequest request = new TableLoginRequest("cafe-01", 99, "1234");

        Store store = Store.builder().name("카페01").code("cafe-01").build();
        ReflectionTestUtils.setField(store, "id", 100L);

        when(storeRepository.findByCode("cafe-01")).thenReturn(Optional.of(store));
        when(tableRepository.findByStoreIdAndTableNumber(100L, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableAuthService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void login_wrongPassword_throwsException() {
        TableLoginRequest request = new TableLoginRequest("cafe-01", 1, "wrong-pw");

        Store store = Store.builder().name("카페01").code("cafe-01").build();
        ReflectionTestUtils.setField(store, "id", 100L);

        TableEntity table = TableEntity.builder().storeId(100L).tableNumber(1).password("encoded-pw").build();
        ReflectionTestUtils.setField(table, "id", 5L);

        when(storeRepository.findByCode("cafe-01")).thenReturn(Optional.of(store));
        when(tableRepository.findByStoreIdAndTableNumber(100L, 1)).thenReturn(Optional.of(table));
        when(passwordEncoder.matches("wrong-pw", "encoded-pw")).thenReturn(false);

        assertThatThrownBy(() -> tableAuthService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
