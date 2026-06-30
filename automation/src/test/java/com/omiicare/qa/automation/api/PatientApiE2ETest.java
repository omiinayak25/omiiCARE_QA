package com.omiicare.qa.automation.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.omiicare.qa.automation.core.adapter.AdapterFactory;
import com.omiicare.qa.automation.core.adapter.ResourceAdapter;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import com.omiicare.qa.automation.core.generators.PatientFactory;
import com.omiicare.qa.automation.core.generators.SyntheticPatient;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * API end-to-end suite (Rest Assured) against the local omiiCARE SUT. Tagged
 * {@code api-e2e}: excluded from the default build and run with {@code -Pe2e}
 * once the backend is up. Demonstrates auth, request building, status/body
 * assertions, and JSON-schema validation through the adapter layer.
 */
@Tag("api-e2e")
class PatientApiE2ETest {

    private static ResourceAdapter sut;
    private static String token;

    @BeforeAll
    static void authenticate() {
        FrameworkConfig config = FrameworkConfig.get();
        sut = AdapterFactory.create(TargetSystem.LOCAL_OMIICARE, config);
        token =
                given().contentType(ContentType.JSON)
                        .body(
                                Map.of(
                                        "username", config.get("omii.sut.username", "demo.admin"),
                                        "password", config.get("omii.sut.password", "Admin@12345")))
                        .post(sut.url("/auth/login"))
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("data.accessToken");
    }

    @Test
    void registersPatientAndMatchesSchema() {
        SyntheticPatient patient = new PatientFactory().newPatient();
        given().header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(
                        Map.of(
                                "firstName", patient.firstName(),
                                "lastName", patient.lastName(),
                                "dateOfBirth", patient.dateOfBirth(),
                                "gender", patient.gender(),
                                "email", patient.email()))
                .post(sut.url("/patients"))
                .then()
                .statusCode(201)
                .body("data.mrn", notNullValue())
                .body(
                        "data",
                        JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/patient-schema.json"));
    }

    @Test
    void rejectsUnauthenticatedAccess() {
        given().get(sut.url("/patients")).then().statusCode(401);
    }

    @Test
    void rejectsInvalidPatientWithProblemDetail() {
        given().header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("firstName", "", "lastName", "X"))
                .post(sut.url("/patients"))
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("OMII-400"));
    }
}
