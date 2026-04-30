package com.tableorder.customer.auth;

import com.tableorder.core.exception.InvalidCredentialsException;
import com.tableorder.customer.auth.dto.TableLoginRequest;
import com.tableorder.customer.auth.dto.TableLoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableAuthControllerTest {

    @Mock
    private TableAuthService tableAuthService;

    private TableAuthController controller;

    @BeforeEach
    void setUp() {
        controller = new TableAuthController(tableAuthService);
    }

    @Test
    @DisplayName("로그인 성공 시 200 OK와 토큰을 반환한다")
    void login_success_returnsOk() {
        TableLoginRequest request = new TableLoginRequest("cafe-01", 1, "1234");
        TableLoginResponse loginResponse = new TableLoginResponse("jwt-token", 100L, 5L, 1);

        when(tableAuthService.login(any())).thenReturn(loginResponse);

        var response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().token()).isEqualTo("jwt-token");
        assertThat(response.getBody().data().storeId()).isEqualTo(100L);
        assertThat(response.getBody().data().tableId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("인증 실패 시 예외가 전파된다")
    void login_invalidCredentials_throwsException() {
        TableLoginRequest request = new TableLoginRequest("cafe-01", 1, "wrong");

        when(tableAuthService.login(any())).thenThrow(new InvalidCredentialsException());

        assertThatThrownBy(() -> controller.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
