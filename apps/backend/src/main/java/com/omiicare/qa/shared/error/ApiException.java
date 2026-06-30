package com.omiicare.qa.shared.error;

/**
 * Base type for all application exceptions that map to a deterministic
 * {@link ErrorCode} and HTTP status. Controllers never handle exceptions
 * directly; the centralized exception handler translates these into RFC 7807
 * Problem Details responses.
 */
public class ApiException extends RuntimeException {

    private final transient ErrorCode errorCode;

    protected ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
