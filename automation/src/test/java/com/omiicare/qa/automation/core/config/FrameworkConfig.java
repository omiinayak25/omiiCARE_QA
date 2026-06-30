package com.omiicare.qa.automation.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Layered configuration for the automation platform. Precedence (highest first): JVM system
 * properties, environment variables, then the bundled {@code framework.properties}. Nothing is
 * hardcoded in tests — every endpoint, credential, and environment name is resolved here.
 */
public final class FrameworkConfig {

    private static final FrameworkConfig INSTANCE = new FrameworkConfig();

    private final Properties fileProperties = new Properties();

    private FrameworkConfig() {
        try (InputStream in =
                getClass().getClassLoader().getResourceAsStream("config/framework.properties")) {
            if (in != null) {
                fileProperties.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load framework.properties", e);
        }
    }

    public static FrameworkConfig get() {
        return INSTANCE;
    }

    /** The active environment name (dev, local, docker, qa, stage). */
    public String environment() {
        return resolve("omii.env", "OMII_ENV", "local");
    }

    /** Base URI for a target system, honoring overrides then the enum default. */
    public String baseUri(TargetSystem system) {
        return resolve(
                system.baseUriProperty(),
                system.baseUriProperty().toUpperCase().replace('.', '_'),
                system.defaultBaseUri());
    }

    public String get(String key, String defaultValue) {
        return resolve(key, key.toUpperCase().replace('.', '_'), defaultValue);
    }

    private String resolve(String systemPropertyKey, String envKey, String defaultValue) {
        String fromSystem = System.getProperty(systemPropertyKey);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return fileProperties.getProperty(systemPropertyKey, defaultValue);
    }
}
