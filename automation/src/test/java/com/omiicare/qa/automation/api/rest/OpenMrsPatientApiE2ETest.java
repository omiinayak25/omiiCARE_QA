package com.omiicare.qa.automation.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * End-to-end checks against the OpenMRS REST patient and encounter endpoints. Requires a reachable
 * OpenMRS Reference Application seeded with demo data, so it is tagged {@code api-e2e} and excluded
 * from the default build.
 */
@Tag("api-e2e")
class OpenMrsPatientApiE2ETest {

    /** A name fragment present in the OpenMRS demo dataset. */
    private static final String SEED_QUERY = "Smith";

    @Test
    @DisplayName("GET /patient?q= returns well-formed patient summaries")
    void searchReturnsPatients() {
        OpenMrsRestClient client = new OpenMrsRestClient();

        List<PatientSummary> results = client.searchPatients(SEED_QUERY);

        assertThat(results).isNotNull();
        assumeThat(results).as("demo dataset must contain a '%s' match", SEED_QUERY).isNotEmpty();
        assertThat(results).allSatisfy(p -> assertThat(p.hasUuid()).isTrue());
    }

    @Test
    @DisplayName("GET /patient/{uuid} round-trips a UUID discovered via search")
    void getPatientByUuidRoundTrips() {
        OpenMrsRestClient client = new OpenMrsRestClient();
        List<PatientSummary> results = client.searchPatients(SEED_QUERY);
        assumeThat(results).as("need at least one seed patient").isNotEmpty();

        String uuid = results.get(0).uuid();
        PatientSummary fetched = client.getPatient(uuid);

        assertThat(fetched).isNotNull();
        assertThat(fetched.uuid()).isEqualTo(uuid);
        assertThat(fetched.display()).isNotBlank();
    }

    @Test
    @DisplayName("GET /patient/{uuid} for an unknown UUID returns null")
    void getPatientUnknownReturnsNull() {
        OpenMrsRestClient client = new OpenMrsRestClient();

        PatientSummary fetched = client.getPatient("00000000-0000-0000-0000-000000000000");

        assertThat(fetched).isNull();
    }

    @Test
    @DisplayName("GET /encounter?patient= returns encounters belonging to that patient")
    void encountersBelongToPatient() {
        OpenMrsRestClient client = new OpenMrsRestClient();
        List<PatientSummary> results = client.searchPatients(SEED_QUERY);
        assumeThat(results).as("need at least one seed patient").isNotEmpty();

        String patientUuid = results.get(0).uuid();
        List<EncounterSummary> encounters = client.getEncountersForPatient(patientUuid);

        assertThat(encounters).isNotNull();
        assertThat(encounters)
                .allSatisfy(
                        e -> {
                            assertThat(e.hasUuid()).isTrue();
                            if (e.patientUuid() != null) {
                                assertThat(e.patientUuid()).isEqualTo(patientUuid);
                            }
                        });
    }
}
