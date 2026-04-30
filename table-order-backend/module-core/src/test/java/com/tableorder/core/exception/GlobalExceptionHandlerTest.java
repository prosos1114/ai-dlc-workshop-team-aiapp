package com.tableorder.core.exception;

import com.tableorder.core.dto.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("NotFoundException 처리 - 404 반환")
    void handleBusinessException_notFound() {
        NotFoundException ex = new NotFoundException("메뉴", 1L);

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("메뉴_NOT_FOUND");
        assertThat(response.getBody().message()).contains("메뉴");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @DisplayName("DuplicateResourceException 처리 - 409 반환")
    void handleBusinessException_duplicate() {
        DuplicateResourceException ex = new DuplicateResourceException("매장", "store-001");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("DUPLICATE_매장");
    }

    @Test
    @DisplayName("InvalidCredentialsException 처리 - 401 반환")
    void handleBusinessException_invalidCredentials() {
        InvalidCredentialsException ex = new InvalidCredentialsException();

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("AccountLockedException 처리 - 429 반환")
    void handleBusinessException_accountLocked() {
        AccountLockedException ex = new AccountLockedException();

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ACCOUNT_LOCKED");
    }

    @Test
    @DisplayName("InvalidStatusTransitionException 처리 - 400 반환")
    void handleBusinessException_invalidStatusTransition() {
        InvalidStatusTransitionException ex = new InvalidStatusTransitionException("PENDING", "COMPLETED");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_STATUS_TRANSITION");
        assertThat(response.getBody().message()).contains("PENDING").contains("COMPLETED");
    }

    @Test
    @DisplayName("NoActiveSessionException 처리 - 400 반환")
    void handleBusinessException_noActiveSession() {
        NoActiveSessionException ex = new NoActiveSessionException(5L);

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("NO_ACTIVE_SESSION");
        assertThat(response.getBody().message()).contains("5");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 - 400 반환")
    void handleValidationException() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));
        bindingResult.addError(new FieldError("target", "price", "must be positive"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().message()).contains("name").contains("price");
    }

    @Test
    @DisplayName("AccessDeniedException 처리 - 403 반환")
    void handleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ApiErrorResponse> response = handler.handleAccessDeniedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ACCESS_DENIED");
    }

    @Test
    @DisplayName("일반 Exception 처리 - 500 반환")
    void handleException() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ApiErrorResponse> response = handler.handleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
    }
}
