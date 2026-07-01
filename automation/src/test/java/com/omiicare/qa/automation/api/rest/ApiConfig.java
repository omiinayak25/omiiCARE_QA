package com.omiicare.qa.automation.api.rest;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Resolves all REST API connection settings from the layered {@link FrameworkConfig}, so no
 * endpoint, credential, or timeout is hardcoded inside a test. Defaults target the OpenMRS
 * Reference Application, but every value can be overridden via system property, environment
 * variable, or {@code framework.properties}.
 *
 * <p>Configuration keys (system-property form):
 *
 * <ul>
 *   <li>{@code omii.api.rest.baseUri} — REST base URI (default: OpenMRS demo {@code .../ws/rest/v1})
 *   <li>{@code omii.api.rest.username} — basic-auth username (default {@code admin})
 *   <li>{@code omii.api.rest.password} — basic-auth password (default {@code Admin123})
 *   <li>{@code omii.api.rest.timeoutMs} — per-request timeout in milliseconds (default {@code 30000})
 * </ul>
 */
public final class ApiConfig {

    /** Configuration key for the REST base URI. */
    public static final String KEY_BASE_URI = "omii.api.rest.baseUri";

    /** Configuration key for the basic-auth username. */
    public static final String KEY_USERNAME = "omii.api.rest.username";

    /** Configuration key for the basic-auth password. */
    public static final String KEY_PASSWORD = "omii.api.rest.password";

    /** Configuration key for the per-request timeout in milliseconds. */
    public static final String KEY_TIMEOUT_MS = "omii.api.rest.timeoutMs";

    private final String baseUri;
    private final String username;
    private final String password;
    private final int timeoutMs;

    private ApiConfig(String baseUri, String username, String password, int timeoutMs) {
        this.baseUri = baseUri;
        this.username = username;
        this.password = password;
        this.timeoutMs = timeoutMs;
    }

    /**
     * Builds an {@link ApiConfig} from the active framework configuration, falling back to the
     * OpenMRS demo system's base URI when no explicit {@code omii.api.rest.baseUri} is provided.
     *
     * @return a fully resolved configuration instance
     */
    public static ApiConfig fromFramework() {
        FrameworkConfig config = FrameworkConfig.get();
        String defaultBase = config.baseUri(TargetSystem.OPENMRS);
        String resolvedBase = config.get(KEY_BASE_URI, defaultBase);
        String user = config.get(KEY_USERNAME, "admin");
        String pass = config.get(KEY_PASSWORD, "Admin123");
        int timeout = parseTimeout(config.get(KEY_TIMEOUT_MS, "30000"));
        return new ApiConfig(normalize(resolvedBase), user, pass, timeout);
    }

    /**
     * Builds an explicit {@link ApiConfig}, primarily for tests that need a deterministic, fully
     * specified configuration without touching the environment.
     *
     * @param baseUri base URI for the REST endpoint
     * @param username basic-auth username
     * @param password basic-auth password
     * @param timeoutMs per-request timeout in milliseconds
     * @return a configuration instance with the supplied values
     */
    public static ApiConfig of(String baseUri, String username, String password, int timeoutMs) {
        return new ApiConfig(normalize(baseUri), username, password, Math.max(1, timeoutMs));
    }

    private static int parseTimeout(String raw) {
        try {
            int parsed = Integer.parseInt(raw.trim());
            return parsed > 0 ? parsed : 30000;
        } catch (NumberFormatException e) {
            return 30000;
        }
    }

    /** Strips any trailing slash so path concatenation stays predictable. */
    static String normalize(String uri) {
        if (uri == null || uri.isBlank()) {
            return "";
        }
        String trimmed = uri.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    public String baseUri() {
        return baseUri;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public int timeoutMs() {
        return timeoutMs;
    }

    @Override
    public String toString() {
        return "ApiConfig{baseUri='" + baseUri + "', username='" + username + "', timeoutMs=" + timeoutMs + '}';
    }
}
