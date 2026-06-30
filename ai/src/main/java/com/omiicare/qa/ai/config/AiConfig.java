package com.omiicare.qa.ai.config;

/**
 * Configuration for the AI platform, resolved (highest precedence first) from JVM system properties
 * then environment variables, with safe defaults. AI is <strong>disabled by default</strong> — it
 * must be explicitly turned on, and a provider call additionally requires an API key. AI assists;
 * humans decide.
 */
public final class AiConfig {

    private final boolean enabled;
    private final String provider;
    private final String model;
    private final String apiKey;

    private AiConfig(boolean enabled, String provider, String model, String apiKey) {
        this.enabled = enabled;
        this.provider = provider;
        this.model = model;
        this.apiKey = apiKey;
    }

    public static AiConfig load() {
        boolean enabled =
                Boolean.parseBoolean(resolve("omii.ai.enabled", "OMII_AI_ENABLED", "false"));
        String provider = resolve("omii.ai.provider", "OMII_AI_PROVIDER", "local");
        String model = resolve("omii.ai.model", "OMII_AI_MODEL", "claude-opus-4-8");
        String apiKey = resolve("omii.ai.apiKey", "OMII_AI_API_KEY", "");
        return new AiConfig(enabled, provider, model, apiKey);
    }

    public static AiConfig of(boolean enabled, String provider, String model, String apiKey) {
        return new AiConfig(enabled, provider, model, apiKey);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String provider() {
        return provider;
    }

    public String model() {
        return model;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String apiKey() {
        return apiKey;
    }

    private static String resolve(String sysProp, String envVar, String defaultValue) {
        String fromSys = System.getProperty(sysProp);
        if (fromSys != null && !fromSys.isBlank()) {
            return fromSys;
        }
        String fromEnv = System.getenv(envVar);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return defaultValue;
    }
}
