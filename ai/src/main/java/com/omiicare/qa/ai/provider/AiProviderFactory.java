package com.omiicare.qa.ai.provider;

import com.omiicare.qa.ai.config.AiConfig;
import java.util.Locale;

/**
 * Resolves the configured {@link AiProvider} (Factory pattern). Selection is
 * configuration-driven and vendor-neutral: {@code local} for offline work, or a
 * hosted provider (Claude / OpenAI) that requires a runtime API key.
 */
public final class AiProviderFactory {

    private AiProviderFactory() {}

    public static AiProvider create(AiConfig config) {
        String provider = config.provider() == null ? "local" : config.provider().toLowerCase(Locale.ROOT);
        return switch (provider) {
            case "claude", "openai" -> new ApiKeyRequiredProvider(provider, config);
            case "local" -> new LocalEchoProvider(config.model());
            default ->
                    throw new IllegalArgumentException(
                            "Unknown AI provider: '" + provider + "' (expected claude|openai|local)");
        };
    }
}
