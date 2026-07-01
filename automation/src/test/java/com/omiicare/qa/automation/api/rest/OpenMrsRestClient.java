package com.omiicare.qa.automation.api.rest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST client for the OpenMRS Reference Application's web-services layer ({@code /ws/rest/v1}).
 * Wraps the raw HTTP interactions behind typed, intention-revealing methods for the endpoints the
 * platform exercises most: session establishment, patient search/retrieval, and encounter
 * retrieval. Authentication is HTTP Basic using the credentials resolved by {@link ApiConfig}.
 *
 * <p>This client performs network I/O; tests that drive it must be tagged so they are excluded from
 * the default build.
 */
public class OpenMrsRestClient extends BaseApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMrsRestClient.class);

    /** Custom representation that expands the fields the summaries project. */
    private static final String PATIENT_REP =
            "custom:(uuid,display,voided,"
                    + "patientIdentifier:(identifier),"
                    + "person:(gender,preferredName:(givenName,familyName)))";

    private static final String ENCOUNTER_REP =
            "custom:(uuid,display,encounterDatetime,voided,"
                    + "patient:(uuid),encounterType:(uuid))";

    /** Creates a client using framework-resolved configuration. */
    public OpenMrsRestClient() {
        super();
    }

    /**
     * Creates a client bound to an explicit configuration.
     *
     * @param config resolved API configuration
     */
    public OpenMrsRestClient(ApiConfig config) {
        super(config);
    }

    /**
     * Authenticates against {@code GET /ws/rest/v1/session} using configured basic-auth credentials
     * and returns the parsed session state.
     *
     * @return a populated {@link SessionInfo}; {@link SessionInfo#authenticated()} is {@code false}
     *     when the credentials are rejected
     */
    public SessionInfo openSession() {
        Response response =
                request()
                        .auth()
                        .preemptive()
                        .basic(config().username(), config().password())
                        .when()
                        .get("/session")
                        .andReturn();

        if (response.statusCode() != 200) {
            LOG.warn("Session request returned HTTP {}", response.statusCode());
            return SessionInfo.anonymous();
        }
        JsonPath json = response.jsonPath();
        boolean authenticated = json.getBoolean("authenticated");
        if (!authenticated) {
            return SessionInfo.anonymous();
        }
        return new SessionInfo(
                true,
                json.getString("user.uuid"),
                json.getString("user.username"),
                json.getString("sessionLocation.uuid"));
    }

    /**
     * Searches patients via {@code GET /ws/rest/v1/patient?q=...}.
     *
     * @param query free-text query (name fragment or identifier); must not be blank
     * @return zero or more matching patients, never {@code null}
     */
    public List<PatientSummary> searchPatients(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Patient search query must not be blank");
        }
        Response response =
                authed()
                        .queryParam("q", query)
                        .queryParam("v", PATIENT_REP)
                        .when()
                        .get("/patient")
                        .andReturn();
        response.then().statusCode(200);

        List<PatientSummary> results = new ArrayList<>();
        JsonPath json = response.jsonPath();
        List<Object> entries = json.getList("results");
        if (entries == null) {
            return results;
        }
        for (int i = 0; i < entries.size(); i++) {
            results.add(parsePatient(json, "results[" + i + "]"));
        }
        return results;
    }

    /**
     * Retrieves a single patient via {@code GET /ws/rest/v1/patient/{uuid}}.
     *
     * @param uuid the patient UUID; must not be blank
     * @return the patient projection, or {@code null} when the server responds 404
     */
    public PatientSummary getPatient(String uuid) {
        requireUuid(uuid, "patient");
        Response response =
                authed()
                        .queryParam("v", PATIENT_REP)
                        .when()
                        .get("/patient/{uuid}", uuid)
                        .andReturn();
        if (response.statusCode() == 404) {
            return null;
        }
        response.then().statusCode(200);
        return parsePatient(response.jsonPath(), "$");
    }

    /**
     * Retrieves encounters for a patient via {@code GET /ws/rest/v1/encounter?patient={uuid}}.
     *
     * @param patientUuid the patient UUID; must not be blank
     * @return zero or more encounters, never {@code null}
     */
    public List<EncounterSummary> getEncountersForPatient(String patientUuid) {
        requireUuid(patientUuid, "patient");
        Response response =
                authed()
                        .queryParam("patient", patientUuid)
                        .queryParam("v", ENCOUNTER_REP)
                        .when()
                        .get("/encounter")
                        .andReturn();
        response.then().statusCode(200);

        List<EncounterSummary> results = new ArrayList<>();
        JsonPath json = response.jsonPath();
        List<Object> entries = json.getList("results");
        if (entries == null) {
            return results;
        }
        for (int i = 0; i < entries.size(); i++) {
            results.add(parseEncounter(json, "results[" + i + "]"));
        }
        return results;
    }

    /**
     * Retrieves a single encounter via {@code GET /ws/rest/v1/encounter/{uuid}}.
     *
     * @param uuid the encounter UUID; must not be blank
     * @return the encounter projection, or {@code null} when the server responds 404
     */
    public EncounterSummary getEncounter(String uuid) {
        requireUuid(uuid, "encounter");
        Response response =
                authed()
                        .queryParam("v", ENCOUNTER_REP)
                        .when()
                        .get("/encounter/{uuid}", uuid)
                        .andReturn();
        if (response.statusCode() == 404) {
            return null;
        }
        response.then().statusCode(200);
        return parseEncounter(response.jsonPath(), "$");
    }

    /** Builds a request specification already carrying preemptive basic auth. */
    private io.restassured.specification.RequestSpecification authed() {
        return request().auth().preemptive().basic(config().username(), config().password());
    }

    private static PatientSummary parsePatient(JsonPath json, String root) {
        String prefix = "$".equals(root) ? "" : root + ".";
        return new PatientSummary(
                json.getString(prefix + "uuid"),
                json.getString(prefix + "display"),
                json.getString(prefix + "patientIdentifier.identifier"),
                json.getString(prefix + "person.preferredName.givenName"),
                json.getString(prefix + "person.preferredName.familyName"),
                json.getString(prefix + "person.gender"),
                Boolean.TRUE.equals(json.getBoolean(prefix + "voided")));
    }

    private static EncounterSummary parseEncounter(JsonPath json, String root) {
        String prefix = "$".equals(root) ? "" : root + ".";
        return new EncounterSummary(
                json.getString(prefix + "uuid"),
                json.getString(prefix + "display"),
                json.getString(prefix + "encounterDatetime"),
                json.getString(prefix + "patient.uuid"),
                json.getString(prefix + "encounterType.uuid"),
                Boolean.TRUE.equals(json.getBoolean(prefix + "voided")));
    }

    private static void requireUuid(String uuid, String what) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException(what + " UUID must not be blank");
        }
    }
}
