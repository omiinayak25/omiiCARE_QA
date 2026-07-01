package com.omiicare.qa.automation.core.env;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolves the effective base URL of a {@link SutTarget} for a single, fixed environment.
 *
 * <p>This class is the environment-scoped view layered on top of the global configuration sources.
 * For a target {@code T} in environment {@code E} the lookup precedence is, highest first:
 *
 * <ol>
 *   <li>JVM system property {@code sut.E.T.baseUrl}</li>
 *   <li>Environment variable {@code SUT_E_T_BASEURL} (dots replaced by underscores, upper-cased)</li>
 *   <li>{@code framework.properties} entry {@code sut.E.T.baseUrl} (via {@code FrameworkConfig})</li>
 *   <li>The target's {@link SutTarget#fallbackBaseUrl() fallback URL}</li>
 * </ol>
 *
 * <p>The configuration lookup is injected as a simple {@code (key, default) -> value} resolver so the
 * class is trivially unit-testable without a live {@code FrameworkConfig} or a running SUT. The
 * production binding is supplied by {@link EnvironmentManager}.
 */
public final class MultiEnvConfig {

    /**
     * Minimal abstraction over a layered configuration source. Implementations must honor system
     * property and environment-variable overlays and fall back to {@code defaultValue} when nothing
     * is configured. {@code FrameworkConfig::get} satisfies this contract directly.
     */
    @FunctionalInterface
    public interface ConfigResolver {
        /**
         * @param key the property key, e.g. {@code sut.qa.openmrs.rest.baseUrl}
         * @param defaultValue value to return when the key is unset across all layers
         * @return the resolved value, or {@code defaultValue}
         */
        String resolve(String key, String defaultValue);
    }

    private final String environment;
    private final ConfigResolver resolver;

    /**
     * @param environment the active environment name (e.g. {@code qa}); must not be blank
     * @param resolver layered configuration resolver; must not be {@code null}
     */
    public MultiEnvConfig(String environment, ConfigResolver resolver) {
        if (environment == null || environment.isBlank()) {
            throw new IllegalArgumentException("environment must not be blank");
        }
        this.environment = environment.trim().toLowerCase(Locale.ROOT);
        this.resolver = Objects.requireNonNull(resolver, "resolver must not be null");
    }

    /**
     * @return the normalized (trimmed, lower-cased) environment name this config is bound to
     */
    public String environment() {
        return environment;
    }

    /**
     * Effective base URL for the given target in this environment, applying full lookup precedence
     * and falling back to {@link SutTarget#fallbackBaseUrl()} when unconfigured.
     *
     * @param target the SUT target; must not be {@code null}
     * @return a non-blank base URL, never {@code null}
     */
    public String baseUrl(SutTarget target) {
        Objects.requireNonNull(target, "target must not be null");
        String key = target.baseUrlProperty(environment);
        String fallback = target.fallbackBaseUrl();
        String value = resolver.resolve(key, fallback);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    /**
     * Effective base URL only if it was explicitly configured (i.e. differs from the built-in
     * fallback). Useful for diagnostics that want to report "using default" versus "overridden".
     *
     * @param target the SUT target; must not be {@code null}
     * @return the configured override, or empty if the fallback is in effect
     */
    public Optional<String> configuredBaseUrl(SutTarget target) {
        Objects.requireNonNull(target, "target must not be null");
        String fallback = target.fallbackBaseUrl();
        String resolved = baseUrl(target);
        return resolved.equals(fallback) ? Optional.empty() : Optional.of(resolved);
    }
}
