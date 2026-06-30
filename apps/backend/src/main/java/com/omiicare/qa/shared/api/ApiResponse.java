package com.omiicare.qa.shared.api;

import java.time.Instant;
import org.slf4j.MDC;

/**
 * Standard success envelope returned by every REST endpoint. Errors use RFC 7807
 * {@link org.springframework.http.ProblemDetail} instead (see the global
 * exception handler), so this type only ever carries successful payloads.
 *
 * @param <T> the payload type
 */
public record ApiResponse<T>(boolean success, T data, String correlationId, Instant timestamp) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, MDC.get("correlationId"), Instant.now());
    }
}
