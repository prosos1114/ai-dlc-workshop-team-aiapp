package com.tableorder.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String CORRELATION_ID = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(CORRELATION_ID, correlationId);

        try {
            log.info("{} {} started", request.getMethod(), request.getRequestURI());
            long startTime = System.currentTimeMillis();

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            log.info("{} {} completed [{}] in {}ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        } finally {
            MDC.clear();
        }
    }
}
