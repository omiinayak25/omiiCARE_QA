package com.omiicare.qa.automation.core.env;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic verification of environment resolution, SUT target lookup, and base-URL precedence.
 *
 * <p>All configuration is supplied via an in-memory {@link MultiEnvConfig.ConfigResolver}, so these
 * tests are deterministic, need no running SUT, and require no mutation of system properties or
 * environment variables. They are intentionally untagged and run in the default {@code mvn test}.
 */
class EnvironmentResolutionTest {

    /** Resolver backed by a simple map, mimicking the {@code (key, default)} contract. */
    private static MultiEnvConfig.ConfigResolver mapResolver(Map<String, String> values) {
        return (key, defaultValue) -> {
            String v = values.get(key);
            return (v == null || v.isBlank()) ? defaultValue : v;
        };
    }

    @Test
    @DisplayName("Active environment defaults to 'local' when none supplied")
    void defaultsToLocal() {
        EnvironmentManager manager = new EnvironmentManager(null, mapResolver(Map.of()));
        assertThat(manager.activeEnvironment()).isEqualTo(EnvironmentManager.DEFAULT_ENVIRONMENT);
        assertThat(manager.activeEnvironment()).isEqualTo("local");
    }

    @Test
    @DisplayName("Explicit environment name is normalized and honored")
    void explicitEnvironmentNormalized() {
        EnvironmentManager manager = new EnvironmentManager("  QA  ", mapResolver(Map.of()));
        assertThat(manager.activeEnvironment()).isEqualTo("qa");
        assertThat(manager.config().environment()).isEqualTo("qa");
    }

    @Test
    @DisplayName("Blank environment falls back to default")
    void blankEnvironmentFallsBack() {
        EnvironmentManager manager = new EnvironmentManager("   ", mapResolver(Map.of()));
        assertThat(manager.activeEnvironment()).isEqualTo("local");
    }

    @Test
    @DisplayName("Unconfigured target resolves to its fallback base URL")
    void fallbackWhenUnconfigured() {
        EnvironmentManager manager = new EnvironmentManager("qa", mapResolver(Map.of()));
        assertThat(manager.baseUrl(SutTarget.OPENMRS_REST))
                .isEqualTo(SutTarget.OPENMRS_REST.fallbackBaseUrl());
        assertThat(manager.config().configuredBaseUrl(SutTarget.OPENMRS_REST)).isEmpty();
    }

    @Test
    @DisplayName("Configured base URL overrides the fallback for the matching environment + target")
    void configuredOverrideWins() {
        Map<String, String> values = new HashMap<>();
        values.put("sut.qa.openmrs.rest.baseUrl", "https://qa.example.org/openmrs/ws/rest/v1");
        EnvironmentManager manager = new EnvironmentManager("qa", mapResolver(values));

        assertThat(manager.baseUrl(SutTarget.OPENMRS_REST))
                .isEqualTo("https://qa.example.org/openmrs/ws/rest/v1");
        assertThat(manager.config().configuredBaseUrl(SutTarget.OPENMRS_REST))
                .contains("https://qa.example.org/openmrs/ws/rest/v1");
    }

    @Test
    @DisplayName("Override is scoped to its environment and does not leak across environments")
    void overrideIsEnvironmentScoped() {
        Map<String, String> values = new HashMap<>();
        values.put("sut.qa.openmrs.rest.baseUrl", "https://qa.example.org/rest");
        // Same target, different active environment -> the qa override must not apply.
        EnvironmentManager stage = new EnvironmentManager("stage", mapResolver(values));
        assertThat(stage.baseUrl(SutTarget.OPENMRS_REST))
                .isEqualTo(SutTarget.OPENMRS_REST.fallbackBaseUrl());
    }

    @Test
    @DisplayName("Blank configured value is ignored in favor of the fallback")
    void blankConfiguredValueIgnored() {
        Map<String, String> values = new HashMap<>();
        values.put("sut.qa.openmrs.fhir.baseUrl", "   ");
        EnvironmentManager manager = new EnvironmentManager("qa", mapResolver(values));
        assertThat(manager.baseUrl(SutTarget.OPENMRS_FHIR))
                .isEqualTo(SutTarget.OPENMRS_FHIR.fallbackBaseUrl());
    }

    @Test
    @DisplayName("Resolved override value is trimmed")
    void resolvedValueTrimmed() {
        Map<String, String> values = new HashMap<>();
        values.put("sut.dev.hapi.fhir.baseUrl", "  https://dev.fhir.example/baseR4  ");
        EnvironmentManager manager = new EnvironmentManager("dev", mapResolver(values));
        assertThat(manager.baseUrl(SutTarget.HAPI_FHIR)).isEqualTo("https://dev.fhir.example/baseR4");
    }

    @Test
    @DisplayName("SutTarget.baseUrlProperty builds the env-scoped key")
    void baseUrlPropertyShape() {
        assertThat(SutTarget.OPENMRS_REST.baseUrlProperty("QA"))
                .isEqualTo("sut.qa.openmrs.rest.baseUrl");
    }

    @Test
    @DisplayName("SutTarget.baseUrlProperty rejects a blank environment")
    void baseUrlPropertyRejectsBlank() {
        assertThatThrownBy(() -> SutTarget.OPENMRS_REST.baseUrlProperty(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("SutTarget.fromToken matches by key and by enum name, case-insensitively")
    void fromTokenResolves() {
        assertThat(SutTarget.fromToken("openmrs.rest")).contains(SutTarget.OPENMRS_REST);
        assertThat(SutTarget.fromToken("OPENMRS_REST")).contains(SutTarget.OPENMRS_REST);
        assertThat(SutTarget.fromToken("OpenMrs.Rest")).contains(SutTarget.OPENMRS_REST);
        assertThat(SutTarget.fromToken("nope")).isEmpty();
        assertThat(SutTarget.fromToken(null)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Every target exposes a non-blank key and fallback URL")
    void targetsAreWellFormed() {
        for (SutTarget t : SutTarget.values()) {
            assertThat(t.key()).isNotBlank();
            assertThat(t.fallbackBaseUrl()).isNotBlank().startsWith("http");
        }
    }

    @Test
    @DisplayName("MultiEnvConfig rejects null resolver and blank environment")
    void multiEnvConfigGuards() {
        assertThatThrownBy(() -> new MultiEnvConfig("qa", null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new MultiEnvConfig(" ", (k, d) -> d))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
