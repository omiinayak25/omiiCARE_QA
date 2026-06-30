package com.omiicare.qa.ai.provider;

/** A single AI request (system + user prompt) and its bounded output size. */
public record AiRequest(String systemPrompt, String userPrompt, int maxTokens) {

    public AiRequest {
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException("userPrompt must not be blank");
        }
        if (maxTokens <= 0) {
            maxTokens = 1024;
        }
    }
}
