package com.omiicare.qa.automation.core.env;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central entry point for resolving the active environment and the base URLs of every
 * {@link SutTarget} within it.
 *
 * <p>The active environment name is resolved with the following precedence, highest first:
 *
 * <ol>
 *   <li>JVM system property {@code omii.env}</li>
 *   <li>Environment variable {@code OMII_ENV}</li>
 *   <li>{@code framework.properties} key {@code omii.env}</li>
 *   <li>The built-in {@link #DEFAULT_ENVIRONMENT default} ({@code local})</li>
 * </ol>
 *
 * <p>This mirrors the layering already implemented by {@link FrameworkConfig#environment()}, which
 * the production constructor delegates to. A second constructor accepts an explicit environment name
 * and {@link MultiEnvConfig.ConfigResolver}, making precedence and URL resolution unit-testable
 * without any system-property or environment-variable mutation and without a live SUT.
 */
public final class EnvironmentManager {

    /** Environment used when no source supplies one. */
    public static final String DEFAULT_ENVIRONMENT = "local";

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentManager.class);

    private final String activeEnvironment;
    private final MultiEnvConfig config;

    /**
     * Production binding: resolves the active environment from {@link FrameworkConfig} and uses
     * {@link FrameworkConfig#get(String, String)} as the layered resolver.
     */
    public EnvironmentManager() {
        this(resolveFromFramework(), FrameworkConfig.get()::get);
    }

    /**
     * Test / advanced binding with an explicit environment and resolver.
     *
     * @param activeEnvironment the active environment name; if blank, {@link #DEFAULT_ENVIRONMENT}
     *     is substituted
     * @param resolver layered configuration resolver; must not be {@code null}
     */
    public EnvironmentManager(String activeEnvironment, MultiEnvConfig.ConfigResolver resolver) {
        Objects.requireNonNull(resolver, "resolver must not be null");
        this.activeEnvironment =
                (activeEnvironment == null || activeEnvironment.isBlank())
                        ? DEFAULT_ENVIRONMENT
                        : activeEnvironment.trim().toLowerCase(Locale.ROOT);
        this.config = new MultiEnvConfig(this.activeEnvironment, resolver);
        LOG.debug("EnvironmentManager initialized for environment '{}'", this.activeEnvironment);
    }

    /**
     * Resolve the active environment name independently of any instance, honoring system property /
     * environment variable / file precedence and finally {@link #DEFAULT_ENVIRONMENT}.
     *
     * @return the normalized active environment name, never blank
     */
    public static String resolveActiveEnvironment() {
        return resolveFromFramework();
    }

    private static String resolveFromFramework() {
        String env = FrameworkConfig.get().environment();
        if (env == null || env.isBlank()) {
            return DEFAULT_ENVIRONMENT;
        }
        return env.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * @return the active environment name this manager is bound to, never blank
     */
    public String activeEnvironment() {
        return activeEnvironment;
    }

    /**
     * @return the environment-scoped {@link MultiEnvConfig} backing this manager
     */
    public MultiEnvConfig config() {
        return config;
    }

    /**
     * Effective base URL of the given target in the active environment.
     *
     * @param target the SUT target; must not be {@code null}
     * @return a non-blank base URL, never {@code null}
     */
    public String baseUrl(SutTarget target) {
        return config.baseUrl(target);
    }
}
