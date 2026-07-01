package com.omiicare.qa.automation.core.env;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Logical System-Under-Test (SUT) targets the automation platform exercises.
 *
 * <p>A {@code SutTarget} is an abstract handle ("the OpenMRS REST API", "the OpenMRS UI") rather than
 * a concrete URL. The actual base URL is resolved per active environment by {@link MultiEnvConfig},
 * so the same suite can run against {@code local}, {@code docker}, {@code qa}, or {@code demo}
 * deployments purely through configuration. Each target carries a stable {@link #key()} used to build
 * configuration property names and an out-of-the-box {@link #fallbackBaseUrl()} that points at the
 * public OpenMRS Reference Application so tests have a sane default when no override is supplied.
 */
public enum SutTarget {

    /** OpenMRS REST API (e.g. {@code /ws/rest/v1}). */
    OPENMRS_REST("openmrs.rest", "https://o2.openmrs.org/openmrs/ws/rest/v1"),

    /** OpenMRS FHIR R4 facade (e.g. {@code /ws/fhir2/R4}). */
    OPENMRS_FHIR("openmrs.fhir", "https://o2.openmrs.org/openmrs/ws/fhir2/R4"),

    /** OpenMRS web UI entry point (login + home page navigation). */
    OPENMRS_UI("openmrs.ui", "https://o2.openmrs.org/openmrs"),

    /** Generic FHIR R4 sandbox used for adapter contract checks. */
    HAPI_FHIR("hapi.fhir", "https://hapi.fhir.org/baseR4"),

    /** OmiiCare first-party API under test. */
    OMIICARE_API("omiicare.api", "http://localhost:8080/api/v1");

    private final String key;
    private final String fallbackBaseUrl;

    SutTarget(String key, String fallbackBaseUrl) {
        this.key = key;
        this.fallbackBaseUrl = fallbackBaseUrl;
    }

    /**
     * Stable, lower-case identifier for this target (e.g. {@code openmrs.rest}). Used as the suffix
     * component when composing environment-scoped configuration keys.
     *
     * @return the logical key, never {@code null}
     */
    public String key() {
        return key;
    }

    /**
     * Base URL used when no environment-specific override is configured. Points at a publicly
     * reachable default (the OpenMRS Reference Application) so the framework is usable out of the box.
     *
     * @return the fallback base URL, never {@code null}
     */
    public String fallbackBaseUrl() {
        return fallbackBaseUrl;
    }

    /**
     * Configuration property name for this target within a given environment, of the form
     * {@code sut.<env>.<key>.baseUrl}. Resolving this property (with system property / environment
     * variable overlays) yields the effective base URL.
     *
     * @param environment the active environment name (e.g. {@code qa}); must not be blank
     * @return the fully-qualified property key
     */
    public String baseUrlProperty(String environment) {
        if (environment == null || environment.isBlank()) {
            throw new IllegalArgumentException("environment must not be blank");
        }
        return "sut." + environment.trim().toLowerCase(Locale.ROOT) + "." + key + ".baseUrl";
    }

    /**
     * Resolve a target from its {@link #key()} or {@link #name() enum name}, case-insensitively.
     *
     * @param token a key such as {@code openmrs.rest} or an enum name such as {@code OPENMRS_REST}
     * @return the matching target, or empty if none matches / the token is blank
     */
    public static Optional<SutTarget> fromToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String normalized = token.trim();
        return Arrays.stream(values())
                .filter(t -> t.key.equalsIgnoreCase(normalized) || t.name().equalsIgnoreCase(normalized))
                .findFirst();
    }
}
