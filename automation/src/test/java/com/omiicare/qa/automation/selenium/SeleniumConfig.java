package com.omiicare.qa.automation.selenium;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.time.Duration;

/**
 * Typed, configuration-driven settings for the Selenium UI layer. Nothing is hardcoded in tests:
 * every value is resolved through {@link FrameworkConfig}, which honors JVM system properties,
 * environment variables, then the bundled {@code framework.properties}.
 *
 * <p>Relevant keys (with defaults):
 *
 * <ul>
 *   <li>{@code selenium.baseUrl} — base URL of the OpenMRS RefApp (default {@code https://o2.openmrs.org/openmrs}).
 *   <li>{@code selenium.browser} — browser name (default {@code chrome}).
 *   <li>{@code selenium.headless} — run headless (default {@code true}).
 *   <li>{@code selenium.windowWidth} / {@code selenium.windowHeight} — window size (default 1920x1080).
 *   <li>{@code selenium.timeout.implicit.seconds} — implicit wait (default 0; explicit waits preferred).
 *   <li>{@code selenium.timeout.explicit.seconds} — explicit wait ceiling (default 30).
 *   <li>{@code selenium.timeout.pageLoad.seconds} — page-load timeout (default 60).
 *   <li>{@code selenium.username} / {@code selenium.password} — RefApp credentials (default Admin/Admin123).
 *   <li>{@code selenium.loginLocation} — session location label fragment (default {@code Inpatient Ward}).
 * </ul>
 */
public final class SeleniumConfig {

    private final FrameworkConfig config;

    private SeleniumConfig(FrameworkConfig config) {
        this.config = config;
    }

    /** Builds a config view backed by the shared {@link FrameworkConfig} singleton. */
    public static SeleniumConfig fromFramework() {
        return new SeleniumConfig(FrameworkConfig.get());
    }

    /** Base URL of the application under test, without a trailing slash. */
    public String baseUrl() {
        String raw = config.get("selenium.baseUrl", "https://o2.openmrs.org/openmrs");
        return raw.endsWith("/") ? raw.substring(0, raw.length() - 1) : raw;
    }

    /** Browser identifier; currently only {@code chrome} is wired in the factory. */
    public String browser() {
        return config.get("selenium.browser", "chrome").trim().toLowerCase();
    }

    /** Whether the browser should run headless. */
    public boolean headless() {
        return Boolean.parseBoolean(config.get("selenium.headless", "true"));
    }

    public int windowWidth() {
        return parseInt(config.get("selenium.windowWidth", "1920"), 1920);
    }

    public int windowHeight() {
        return parseInt(config.get("selenium.windowHeight", "1080"), 1080);
    }

    public Duration implicitWait() {
        return Duration.ofSeconds(parseLong(config.get("selenium.timeout.implicit.seconds", "0"), 0));
    }

    public Duration explicitWait() {
        return Duration.ofSeconds(parseLong(config.get("selenium.timeout.explicit.seconds", "30"), 30));
    }

    public Duration pageLoadTimeout() {
        return Duration.ofSeconds(parseLong(config.get("selenium.timeout.pageLoad.seconds", "60"), 60));
    }

    public String username() {
        return config.get("selenium.username", "Admin");
    }

    public String password() {
        return config.get("selenium.password", "Admin123");
    }

    /** Fragment of the session-location label to select on login (matched case-insensitively). */
    public String loginLocation() {
        return config.get("selenium.loginLocation", "Inpatient Ward");
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static long parseLong(String value, long fallback) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
