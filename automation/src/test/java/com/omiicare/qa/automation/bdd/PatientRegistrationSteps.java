package com.omiicare.qa.automation.bdd;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.adapter.AdapterFactory;
import com.omiicare.qa.automation.core.adapter.ResourceAdapter;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import com.omiicare.qa.automation.core.generators.PatientFactory;
import com.omiicare.qa.automation.core.generators.SyntheticPatient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Cucumber step definitions for patient registration (glue package referenced by
 * junit-platform.properties). Reusable steps interacting with the SUT through the
 * adapter layer. Tagged {@code @bdd} in the feature so excluded by default.
 */
public class PatientRegistrationSteps {

    private final ResourceAdapter sut =
            AdapterFactory.create(TargetSystem.LOCAL_OMIICARE, FrameworkConfig.get());
    private String token;
    private Response response;

    @Given("the omiiCARE API is available")
    public void apiAvailable() {
        given().get(sut.url("/../actuator/health")).then().statusCode(200);
    }

    @Given("I am authenticated as the demo administrator")
    public void authenticate() {
        FrameworkConfig config = FrameworkConfig.get();
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

    @When("I register a synthetic patient")
    public void registerSyntheticPatient() {
        SyntheticPatient patient = new PatientFactory().newPatient();
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", patient.firstName());
        body.put("lastName", patient.lastName());
        body.put("dateOfBirth", patient.dateOfBirth());
        body.put("gender", patient.gender());
        response = postPatient(body);
    }

    @When("I register a patient without a last name")
    public void registerInvalidPatient() {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Test");
        body.put("dateOfBirth", "1990-01-01");
        body.put("gender", "OTHER");
        response = postPatient(body);
    }

    @Then("the patient is created with a medical record number")
    public void patientCreated() {
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("data.mrn")).isNotBlank();
    }

    @Then("the API responds with a validation error")
    public void validationError() {
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("errorCode")).isEqualTo("OMII-400");
    }

    private Response postPatient(Map<String, Object> body) {
        return given().header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
                .post(sut.url("/patients"));
    }
}
