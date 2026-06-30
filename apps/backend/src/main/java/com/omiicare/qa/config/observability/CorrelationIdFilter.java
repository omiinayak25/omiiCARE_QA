package com.omiicare.qa.config.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Establishes correlation and request identifiers for every inbound request and
 * publishes them to the SLF4J {@link MDC} so they appear in every structured log
 * line and propagate to downstream spans.
 *
 * <p>This is the Milestone 2 observability foundation. Distributed tracing
 * (trace/span IDs) is contributed by Micrometer Tracing → OpenTelemetry; this
 * filter guarantees a stable correlation ID even before a trace context exists
 * and echoes it back to the caller for end-to-end debugging.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = resolveOrGenerate(request.getHeader(CORRELATION_ID_HEADER));
        String requestId = UUID.randomUUID().toString();
        try {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            MDC.put(REQUEST_ID_MDC_KEY, requestId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            response.setHeader(REQUEST_ID_HEADER, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    private String resolveOrGenerate(String incoming) {
        return StringUtils.hasText(incoming) ? incoming : UUID.randomUUID().toString();
    }
}
