package com.tableorder.admin.auth;

import com.tableorder.admin.auth.dto.AdminResponse;
import com.tableorder.admin.auth.dto.TokenResponse;
import com.tableorder.core.exception.AccountLockedException;
import com.tableorder.core.exception.DuplicateResourceException;
import com.tableorder.core.exception.InvalidCredentialsException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.core.security.JwtTokenProvider;
import com.tableorder.domain.admin.Admin;
import com.tableorder.domain.admin.AdminRepository;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock private StoreRepository storeRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    private AdminAuthService adminAuthService;

    private Store store;
    private Admin admin;

    @BeforeEach
    void setUp() {
        adminAuthService = new AdminAuthService(
                storeRepository, adminRepository, passwordEncoder, jwtTokenProvider, 57600000L);

        store = Store.builder().name("테스트매장").code("test-store").build();
        ReflectionTestUtils.setField(store, "id", 1L);

        admin = Admin.builder().storeId(1L).username("admin1").password("encoded").build();
        ReflectionTestUtils.setField(admin, "id", 1L);
    }

    @Test
    @DisplayName("로그인 성공 시 토큰을 반환한다")
    void login_success() {
        given(storeRepository.findByCode("test-store")).willReturn(Optional.of(store));
        given(adminRepository.findByStoreIdAndUsername(1L, "admin1")).willReturn(Optional.of(admin));
        given(passwordEncoder.matches("password", "encoded")).willReturn(true);
        given(jwtTokenProvider.createAdminToken(1L, 1L)).willReturn("jwt-token");

        TokenResponse result = adminAuthService.login("test-store", "admin1", "password");

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.expiresIn()).isEqualTo(57600000L);
        verify(adminRepository).save(admin);
    }

    @Test
    @DisplayName("존재하지 않는 매장 코드로 로그인 시 InvalidCredentialsException")
    void login_invalidStoreCode() {
        given(storeRepository.findByCode("invalid")).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.login("invalid", "admin1", "password"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 실패 횟수가 증가한다")
    void login_wrongPassword_incrementsAttempts() {
        given(storeRepository.findByCode("test-store")).willReturn(Optional.of(store));
        given(adminRepository.findByStoreIdAndUsername(1L, "admin1")).willReturn(Optional.of(admin));
        given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

        assertThatThrownBy(() -> adminAuthService.login("test-store", "admin1", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);

        assertThat(admin.getLoginAttempts()).isEqualTo(1);
        verify(adminRepository).save(admin);
    }

    @Test
    @DisplayName("잠긴 계정으로 로그인 시 AccountLockedException")
    void login_lockedAccount() {
        admin.lock(java.time.LocalDateTime.now().plusMinutes(15));
        given(storeRepository.findByCode("test-store")).willReturn(Optional.of(store));
        given(adminRepository.findByStoreIdAndUsername(1L, "admin1")).willReturn(Optional.of(admin));

        assertThatThrownBy(() -> adminAuthService.login("test-store", "admin1", "password"))
                .isInstanceOf(AccountLockedException.class);
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        given(storeRepository.findByCode("test-store")).willReturn(Optional.of(store));
        given(adminRepository.existsByStoreIdAndUsername(1L, "newadmin")).willReturn(false);
        given(passwordEncoder.encode("password1")).willReturn("encoded");
        given(adminRepository.save(any(Admin.class))).willAnswer(invocation -> {
            Admin saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 2L);
            return saved;
        });

        AdminResponse result = adminAuthService.register("test-store", "newadmin", "password1");

        assertThat(result.username()).isEqualTo("newadmin");
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    @DisplayName("존재하지 않는 매장에 회원가입 시 NotFoundException")
    void register_storeNotFound() {
        given(storeRepository.findByCode("invalid")).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.register("invalid", "admin", "password1"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("중복 사용자명으로 회원가입 시 DuplicateResourceException")
    void register_duplicateUsername() {
        given(storeRepository.findByCode("test-store")).willReturn(Optional.of(store));
        given(adminRepository.existsByStoreIdAndUsername(1L, "admin1")).willReturn(true);

        assertThatThrownBy(() -> adminAuthService.register("test-store", "admin1", "password1"))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
