package com.tableorder.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    @Mock
    private FilterChain filterChain;

    private RequestLoggingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new RequestLoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("필터 실행 시 correlationId가 MDC에 설정된다")
    void doFilter_setsCorrelationIdInMDC() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        doAnswer(invocation -> {
            String correlationId = MDC.get("correlationId");
            assertThat(correlationId).isNotNull().hasSize(8);
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("필터 실행 후 MDC가 정리된다")
    void doFilter_clearsMDCAfterExecution() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/orders");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    @DisplayName("예외 발생 시에도 MDC가 정리된다")
    void doFilter_clearsMDCOnException() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/error");

        doAnswer(invocation -> {
            throw new ServletException("Test exception");
        }).when(filterChain).doFilter(request, response);

        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (ServletException ignored) {
        }

        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    @DisplayName("filterChain.doFilter가 호출된다")
    void doFilter_callsFilterChain() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/menus");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
