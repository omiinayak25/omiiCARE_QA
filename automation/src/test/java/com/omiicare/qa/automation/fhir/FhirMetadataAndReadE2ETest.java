package com.omiicare.qa.automation.fhir;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * End-to-end FHIR R4 assertions against a live FHIR server (default: OpenMRS RefApp).
 *
 * <p>These tests require network access to a running FHIR endpoint and are therefore tagged
 * {@code api-e2e} so they are excluded from the default {@code mvn test} build. They exercise the
 * {@link FhirClient} transport and the {@link FhirAssertions} validation layer together.
 */
@Tag("api-e2e")
@DisplayName("FHIR R4 metadata + Patient read (live SUT)")
class FhirMetadataAndReadE2ETest {

    private final FhirClient client = new FhirClient();

    @Test
    @DisplayName("CapabilityStatement publishes FHIR version 4.0.1")
    void metadataIsR4CapabilityStatement() {
        Response response = client.metadata();
        assertThat(response.statusCode()).as("metadata HTTP status").isEqualTo(200);

        FhirAssertions.assertThatResource(response)
                .hasFhirVersionR4()
                .hasRequiredFields("status", "rest");
    }

    @Test
    @DisplayName("Patient search returns a Bundle and the first entry reads back by id")
    void patientReadReturnsRequestedResource() {
        // Locate any Patient via search so the test is not pinned to a fixed id.
        Response searchResponse = client.search("Patient", "_count", "1");
        assertThat(searchResponse.statusCode()).as("Patient search HTTP status").isEqualTo(200);

        JsonPath searchJson = searchResponse.jsonPath();
        assertThat(searchJson.getString("resourceType"))
                .as("search resourceType")
                .isEqualTo("Bundle");

        String patientId = searchJson.getString("entry[0].resource.id");
        assertThat(patientId).as("first Patient id from search").isNotBlank();

        Response readResponse = client.read("Patient", patientId);
        assertThat(readResponse.statusCode()).as("Patient read HTTP status").isEqualTo(200);

        FhirAssertions.assertThatResource(readResponse)
                .isNotOperationOutcome()
                .hasResourceType("Patient")
                .hasId(patientId)
                .hasRequiredFields("id");
    }
}
