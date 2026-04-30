package com.tableorder.domain.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AdminTest {

    @Test
    @DisplayName("Admin 생성 시 loginAttempts는 0이다")
    void create_initialLoginAttemptsIsZero() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        assertThat(admin.getLoginAttempts()).isEqualTo(0);
        assertThat(admin.getLockedUntil()).isNull();
    }

    @Test
    @DisplayName("로그인 시도 횟수를 증가시킬 수 있다")
    void incrementLoginAttempts_increases() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        admin.incrementLoginAttempts();
        admin.incrementLoginAttempts();
        admin.incrementLoginAttempts();

        assertThat(admin.getLoginAttempts()).isEqualTo(3);
    }

    @Test
    @DisplayName("로그인 시도 횟수를 초기화할 수 있다")
    void resetLoginAttempts_resetsToZero() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        admin.incrementLoginAttempts();
        admin.incrementLoginAttempts();
        admin.lock(LocalDateTime.now().plusMinutes(30));

        admin.resetLoginAttempts();

        assertThat(admin.getLoginAttempts()).isEqualTo(0);
        assertThat(admin.getLockedUntil()).isNull();
    }

    @Test
    @DisplayName("계정을 잠글 수 있다")
    void lock_setsLockedUntil() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(30);
        admin.lock(lockUntil);

        assertThat(admin.getLockedUntil()).isEqualTo(lockUntil);
    }

    @Test
    @DisplayName("잠금 시간이 미래이면 isLocked()는 true를 반환한다")
    void isLocked_futureTime_returnsTrue() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        admin.lock(LocalDateTime.now().plusMinutes(30));

        assertThat(admin.isLocked()).isTrue();
    }

    @Test
    @DisplayName("잠금 시간이 과거이면 isLocked()는 false를 반환한다")
    void isLocked_pastTime_returnsFalse() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        admin.lock(LocalDateTime.now().minusMinutes(1));

        assertThat(admin.isLocked()).isFalse();
    }

    @Test
    @DisplayName("lockedUntil이 null이면 isLocked()는 false를 반환한다")
    void isLocked_nullLockedUntil_returnsFalse() {
        Admin admin = Admin.builder()
                .storeId(1L)
                .username("admin1")
                .password("encoded-password")
                .build();

        assertThat(admin.isLocked()).isFalse();
    }

    @Test
    @DisplayName("Admin 필드가 올바르게 설정된다")
    void create_fieldsSetCorrectly() {
        Admin admin = Admin.builder()
                .storeId(5L)
                .username("manager")
                .password("hashed-pw")
                .build();

        assertThat(admin.getStoreId()).isEqualTo(5L);
        assertThat(admin.getUsername()).isEqualTo("manager");
        assertThat(admin.getPassword()).isEqualTo("hashed-pw");
    }
}
