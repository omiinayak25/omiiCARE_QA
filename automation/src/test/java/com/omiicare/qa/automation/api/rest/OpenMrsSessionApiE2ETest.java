package com.omiicare.qa.automation.api.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * End-to-end checks against the OpenMRS REST session endpoint. Requires a reachable OpenMRS
 * Reference Application (default: the public demo) and valid credentials, so it is tagged
 * {@code api-e2e} and excluded from the default build.
 */
@Tag("api-e2e")
class OpenMrsSessionApiE2ETest {

    @Test
    @DisplayName("GET /session with valid credentials authenticates and returns a user identity")
    void openSessionAuthenticates() {
        OpenMrsRestClient client = new OpenMrsRestClient();

        SessionInfo session = client.openSession();

        assertThat(session.authenticated()).isTrue();
        assertThat(session.userUuid()).isNotBlank();
        assertThat(session.username()).isNotBlank();
    }

    @Test
    @DisplayName("GET /session with invalid credentials yields an unauthenticated session")
    void openSessionRejectsBadCredentials() {
        ApiConfig good = ApiConfig.fromFramework();
        ApiConfig bad = ApiConfig.of(good.baseUri(), "not-a-real-user", "wrong-password", good.timeoutMs());
        OpenMrsRestClient client = new OpenMrsRestClient(bad);

        SessionInfo session = client.openSession();

        assertThat(session.authenticated()).isFalse();
        assertThat(session.userUuid()).isNull();
    }
}
