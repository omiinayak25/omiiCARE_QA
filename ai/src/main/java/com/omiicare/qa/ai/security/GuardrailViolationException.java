package com.omiicare.qa.ai.security;

/** Thrown when AI input violates a security guardrail (e.g. contains a secret). */
public class GuardrailViolationException extends RuntimeException {

    public GuardrailViolationException(String message) {
        super(message);
    }
}
