package com.omiicare.qa.ai.provider;

/**
 * The result of an AI completion. {@code aiAssisted} is always {@code true} so
 * consumers can clearly label output as AI-generated until a human reviews it.
 */
public record AiResponse(String text, String providerName, String model, boolean aiAssisted) {

    public static AiResponse of(String text, String providerName, String model) {
        return new AiResponse(text, providerName, model, true);
    }
}
