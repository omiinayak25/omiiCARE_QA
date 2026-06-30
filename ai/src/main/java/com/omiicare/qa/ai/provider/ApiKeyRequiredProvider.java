package com.omiicare.qa.ai.provider;

import com.omiicare.qa.ai.config.AiConfig;

/**
 * Represents a hosted provider (Claude or OpenAI). This module bundles no vendor SDK and makes no
 * network calls; constructing the provider is always safe, but {@link #complete} fails fast with a
 * clear message unless an API key is configured. A real integration plugs the vendor SDK in here
 * without changing any caller — the abstraction stays vendor-neutral.
 */
public class ApiKeyRequiredProvider implements AiProvider {

    private final String name;
    private final AiConfig config;

    public ApiKeyRequiredProvider(String name, AiConfig config) {
        this.name = name;
        this.config = config;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public AiResponse complete(AiRequest request) {
        if (!config.hasApiKey()) {
            throw new IllegalStateException(
                    "Provider '"
                            + name
                            + "' requires an API key. Set omii.ai.apiKey / OMII_AI_API_KEY, or use"
                            + " the 'local' provider for offline work. No call was made.");
        }
        // A real SDK call would go here. Intentionally not implemented offline to
        // avoid bundling a vendor SDK or transmitting anything.
        throw new UnsupportedOperationException(
                "Live '" + name + "' calls are wired at deployment time with the vendor SDK.");
    }
}
