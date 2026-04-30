package com.tableorder.core.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET = "my-super-secret-key-for-jwt-token-generation-min-32-chars";
    private static final long ADMIN_EXPIRATION = 57600000L; // 16 hours
    private static final long TABLE_EXPIRATION = 31536000000L; // 1 year

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, ADMIN_EXPIRATION, TABLE_EXPIRATION);
    }

    @Nested
    @DisplayName("Admin 토큰 생성")
    class CreateAdminToken {

        @Test
        @DisplayName("유효한 Admin 토큰을 생성한다")
        void createAdminToken_success() {
            String token = jwtTokenProvider.createAdminToken(1L, 100L);

            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("Admin 토큰에서 subjectId를 추출한다")
        void createAdminToken_extractSubjectId() {
            String token = jwtTokenProvider.createAdminToken(42L, 100L);

            assertThat(jwtTokenProvider.getSubjectId(token)).isEqualTo(42L);
        }

        @Test
        @DisplayName("Admin 토큰에서 storeId를 추출한다")
        void createAdminToken_extractStoreId() {
            String token = jwtTokenProvider.createAdminToken(1L, 200L);

            assertThat(jwtTokenProvider.getStoreId(token)).isEqualTo(200L);
        }

        @Test
        @DisplayName("Admin 토큰의 role은 ADMIN이다")
        void createAdminToken_roleIsAdmin() {
            String token = jwtTokenProvider.createAdminToken(1L, 100L);

            assertThat(jwtTokenProvider.getRole(token)).isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("Table 토큰 생성")
    class CreateTableToken {

        @Test
        @DisplayName("유효한 Table 토큰을 생성한다")
        void createTableToken_success() {
            String token = jwtTokenProvider.createTableToken(5L, 100L);

            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("Table 토큰에서 subjectId를 추출한다")
        void createTableToken_extractSubjectId() {
            String token = jwtTokenProvider.createTableToken(7L, 100L);

            assertThat(jwtTokenProvider.getSubjectId(token)).isEqualTo(7L);
        }

        @Test
        @DisplayName("Table 토큰의 role은 TABLE이다")
        void createTableToken_roleIsTable() {
            String token = jwtTokenProvider.createTableToken(5L, 100L);

            assertThat(jwtTokenProvider.getRole(token)).isEqualTo("TABLE");
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class ValidateToken {

        @Test
        @DisplayName("유효한 토큰은 true를 반환한다")
        void validateToken_validToken_returnsTrue() {
            String token = jwtTokenProvider.createAdminToken(1L, 100L);

            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("잘못된 형식의 토큰은 false를 반환한다")
        void validateToken_malformedToken_returnsFalse() {
            assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
        }

        @Test
        @DisplayName("빈 문자열 토큰은 false를 반환한다")
        void validateToken_emptyToken_returnsFalse() {
            assertThat(jwtTokenProvider.validateToken("")).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰은 false를 반환한다")
        void validateToken_expiredToken_returnsFalse() {
            JwtTokenProvider shortLivedProvider = new JwtTokenProvider(SECRET, -1000L, -1000L);
            String token = shortLivedProvider.createAdminToken(1L, 100L);

            assertThat(jwtTokenProvider.validateToken(token)).isFalse();
        }

        @Test
        @DisplayName("다른 secret으로 서명된 토큰은 false를 반환한다")
        void validateToken_differentSecret_returnsFalse() {
            JwtTokenProvider otherProvider = new JwtTokenProvider(
                    "another-secret-key-that-is-at-least-32-characters-long", ADMIN_EXPIRATION, TABLE_EXPIRATION);
            String token = otherProvider.createAdminToken(1L, 100L);

            assertThat(jwtTokenProvider.validateToken(token)).isFalse();
        }
    }

    @Nested
    @DisplayName("Claims 추출")
    class GetClaims {

        @Test
        @DisplayName("토큰에서 전체 Claims를 추출한다")
        void getClaims_success() {
            String token = jwtTokenProvider.createAdminToken(10L, 50L);

            Claims claims = jwtTokenProvider.getClaims(token);

            assertThat(claims.getSubject()).isEqualTo("10");
            assertThat(claims.get("storeId", Long.class)).isEqualTo(50L);
            assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiration()).isNotNull();
        }

        @Test
        @DisplayName("잘못된 토큰으로 Claims 추출 시 예외가 발생한다")
        void getClaims_invalidToken_throwsException() {
            assertThatThrownBy(() -> jwtTokenProvider.getClaims("invalid-token"))
                    .isInstanceOf(Exception.class);
        }
    }
}
