package com.tableorder.core.interceptor;

import com.tableorder.core.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StoreAccessInterceptorTest {

    private StoreAccessInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new StoreAccessInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증되지 않은 요청은 통과시킨다")
    void preHandle_noAuthentication_passes() throws Exception {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("storeId path variable이 없으면 통과시킨다")
    void preHandle_noStoreIdPathVariable_passes() throws Exception {
        setAuthentication(1L, 100L, "ADMIN");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("tableId", "5"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("요청한 storeId와 인증된 사용자의 storeId가 일치하면 통과시킨다")
    void preHandle_matchingStoreId_passes() throws Exception {
        setAuthentication(1L, 100L, "ADMIN");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("storeId", "100"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("요청한 storeId와 인증된 사용자의 storeId가 다르면 403을 반환한다")
    void preHandle_mismatchingStoreId_returns403() throws Exception {
        setAuthentication(1L, 100L, "ADMIN");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("storeId", "999"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("TABLE role도 storeId 검증을 수행한다")
    void preHandle_tableRole_matchingStoreId_passes() throws Exception {
        setAuthentication(5L, 200L, "TABLE");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("storeId", "200"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("path variables attribute가 null이면 통과시킨다")
    void preHandle_nullPathVariables_passes() throws Exception {
        setAuthentication(1L, 100L, "ADMIN");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    private void setAuthentication(Long subjectId, Long storeId, String role) {
        AuthenticatedUser user = new AuthenticatedUser(subjectId, storeId, role);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
