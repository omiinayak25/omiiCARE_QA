package com.omiicare.qa.shared.error;

/** Thrown when an operation conflicts with existing state (e.g. duplicate key). */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
