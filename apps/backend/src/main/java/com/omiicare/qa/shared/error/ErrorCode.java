package com.omiicare.qa.shared.error;

import org.springframework.http.HttpStatus;

/**
 * Canonical, stable error codes surfaced to API clients. Each maps to an HTTP status and a
 * human-readable default title. Codes are part of the public API contract and must not be
 * renumbered once released.
 */
public enum ErrorCode {
    VALIDATION_FAILED("OMII-400", HttpStatus.BAD_REQUEST, "Validation failed"),
    MALFORMED_REQUEST("OMII-400-1", HttpStatus.BAD_REQUEST, "Malformed request"),
    UNAUTHENTICATED("OMII-401", HttpStatus.UNAUTHORIZED, "Authentication required"),
    INVALID_CREDENTIALS("OMII-401-1", HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    ACCESS_DENIED("OMII-403", HttpStatus.FORBIDDEN, "Access denied"),
    RESOURCE_NOT_FOUND("OMII-404", HttpStatus.NOT_FOUND, "Resource not found"),
    CONFLICT("OMII-409", HttpStatus.CONFLICT, "Conflicting state"),
    BUSINESS_RULE_VIOLATION("OMII-422", HttpStatus.UNPROCESSABLE_ENTITY, "Business rule violation"),
    INTERNAL_ERROR("OMII-500", HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");

    private final String code;
    private final HttpStatus status;
    private final String title;

    ErrorCode(String code, HttpStatus status, String title) {
        this.code = code;
        this.status = status;
        this.title = title;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String title() {
        return title;
    }
}
