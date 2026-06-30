package com.omiicare.qa.shared.error;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception framework. No controller handles exceptions directly;
 * every error is translated here into a consistent RFC 7807 {@link ProblemDetail}
 * carrying the canonical {@link ErrorCode}, the correlation ID, and (for
 * validation) per-field details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handleApi(ApiException ex, HttpServletRequest request) {
        ErrorCode code = ex.errorCode();
        ProblemDetail problem = base(code, ex.getMessage(), request);
        if (ex instanceof BusinessRuleException bre) {
            problem.setProperty("ruleId", bre.ruleId());
        }
        if (code.status().is5xxServerError()) {
            log.error("Server error [{}]: {}", code.code(), ex.getMessage(), ex);
        }
        return ResponseEntity.status(code.status()).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem =
                base(ErrorCode.VALIDATION_FAILED, "One or more fields are invalid", request);
        List<Object> fieldErrors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(
                    java.util.Map.of(
                            "field", fe.getField(),
                            "message", fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()));
        }
        problem.setProperty("errors", fieldErrors);
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.status()).body(problem);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuth(
            AuthenticationException ex, HttpServletRequest request) {
        ProblemDetail problem = base(ErrorCode.UNAUTHENTICATED, ex.getMessage(), request);
        return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.status()).body(problem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        ProblemDetail problem = base(ErrorCode.ACCESS_DENIED, ex.getMessage(), request);
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.status()).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        ProblemDetail problem =
                base(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred", request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ProblemDetail base(ErrorCode code, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(code.status(), detail);
        problem.setTitle(code.title());
        problem.setType(java.net.URI.create("https://omiicare.example/errors/" + code.code()));
        problem.setProperty("errorCode", code.code());
        problem.setProperty("correlationId", MDC.get("correlationId"));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}
