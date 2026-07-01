package com.omiicare.qa.automation.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for {@link ApiConfig} and the value records. These touch no network and no
 * SUT, so they run as part of the default build and must always pass.
 */
class ApiConfigTest {

    @Test
    @DisplayName("of() retains explicit values and normalizes a trailing slash")
    void ofRetainsValues() {
        ApiConfig config = ApiConfig.of("https://host/ws/rest/v1/", "user", "pass", 5000);

        assertThat(config.baseUri()).isEqualTo("https://host/ws/rest/v1");
        assertThat(config.username()).isEqualTo("user");
        assertThat(config.password()).isEqualTo("pass");
        assertThat(config.timeoutMs()).isEqualTo(5000);
    }

    @Test
    @DisplayName("normalize() strips multiple trailing slashes and handles blanks")
    void normalizeStripsSlashes() {
        assertThat(ApiConfig.normalize("http://x/api///")).isEqualTo("http://x/api");
        assertThat(ApiConfig.normalize("http://x/api")).isEqualTo("http://x/api");
        assertThat(ApiConfig.normalize("  ")).isEmpty();
        assertThat(ApiConfig.normalize(null)).isEmpty();
    }

    @Test
    @DisplayName("of() clamps a non-positive timeout to at least 1ms")
    void ofClampsTimeout() {
        assertThat(ApiConfig.of("http://x", "u", "p", 0).timeoutMs()).isEqualTo(1);
        assertThat(ApiConfig.of("http://x", "u", "p", -10).timeoutMs()).isEqualTo(1);
    }

    @Test
    @DisplayName("fromFramework() resolves a usable OpenMRS-style configuration by default")
    void fromFrameworkResolvesDefaults() {
        ApiConfig config = ApiConfig.fromFramework();

        assertThat(config.baseUri()).isNotBlank();
        assertThat(config.baseUri()).doesNotEndWith("/");
        assertThat(config.username()).isNotBlank();
        assertThat(config.timeoutMs()).isPositive();
    }

    @Test
    @DisplayName("SessionInfo.anonymous() is unauthenticated with null identity")
    void anonymousSession() {
        SessionInfo session = SessionInfo.anonymous();

        assertThat(session.authenticated()).isFalse();
        assertThat(session.userUuid()).isNull();
        assertThat(session.username()).isNull();
        assertThat(session.sessionLocationUuid()).isNull();
    }

    @Test
    @DisplayName("PatientSummary.hasUuid() reflects presence of a non-blank UUID")
    void patientHasUuid() {
        assertThat(new PatientSummary("abc", null, null, null, null, null, false).hasUuid()).isTrue();
        assertThat(new PatientSummary(" ", null, null, null, null, null, false).hasUuid()).isFalse();
        assertThat(new PatientSummary(null, null, null, null, null, null, false).hasUuid()).isFalse();
    }

    @Test
    @DisplayName("EncounterSummary.hasUuid() reflects presence of a non-blank UUID")
    void encounterHasUuid() {
        assertThat(new EncounterSummary("e1", null, null, null, null, false).hasUuid()).isTrue();
        assertThat(new EncounterSummary("", null, null, null, null, false).hasUuid()).isFalse();
    }

    @Test
    @DisplayName("BaseApiClient rejects a null configuration")
    void baseClientRejectsNull() {
        assertThatThrownBy(() -> new BaseApiClient(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("OpenMrsRestClient rejects blank query and UUID arguments")
    void clientValidatesArguments() {
        OpenMrsRestClient client =
                new OpenMrsRestClient(ApiConfig.of("http://localhost/ws/rest/v1", "u", "p", 1000));

        assertThatThrownBy(() -> client.searchPatients(" "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> client.getPatient(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> client.getEncounter(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> client.getEncountersForPatient(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
