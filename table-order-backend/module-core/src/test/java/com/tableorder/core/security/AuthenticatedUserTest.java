package com.tableorder.core.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserTest {

    @Test
    @DisplayName("ADMIN role인 경우 isAdmin()은 true를 반환한다")
    void isAdmin_adminRole_returnsTrue() {
        AuthenticatedUser user = new AuthenticatedUser(1L, 100L, "ADMIN");

        assertThat(user.isAdmin()).isTrue();
        assertThat(user.isTable()).isFalse();
    }

    @Test
    @DisplayName("TABLE role인 경우 isTable()은 true를 반환한다")
    void isTable_tableRole_returnsTrue() {
        AuthenticatedUser user = new AuthenticatedUser(5L, 200L, "TABLE");

        assertThat(user.isTable()).isTrue();
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("record 필드에 정상적으로 접근할 수 있다")
    void recordFields_accessible() {
        AuthenticatedUser user = new AuthenticatedUser(42L, 300L, "ADMIN");

        assertThat(user.subjectId()).isEqualTo(42L);
        assertThat(user.storeId()).isEqualTo(300L);
        assertThat(user.role()).isEqualTo("ADMIN");
    }
}
