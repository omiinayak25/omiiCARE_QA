package com.omiicare.qa.automation.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber step definitions for reading FHIR R4 {@code Patient} resources and inspecting the server
 * {@code CapabilityStatement}.
 *
 * <p>To keep the glue compile-safe and runnable without a live FHIR server, the resource payloads
 * are produced by an in-memory simulator and parsed with RestAssured's {@link JsonPath} (no Jackson
 * / HAPI). When wired to a real SUT the {@code fetch*} helpers would issue {@code given().get(...)}
 * against {@code /ws/fhir2/R4} and the parsing/assertions below remain unchanged. Tagged {@code
 * @bdd} in the feature so excluded from the default build.
 */
public class FhirPatientReadSteps {

    private static final Logger LOG = LoggerFactory.getLogger(FhirPatientReadSteps.class);

    private String fhirBasePath;
    private String patientId;
    private JsonPath resource;

    @Given("the FHIR R4 base path is {string}")
    public void fhirBasePath(String basePath) {
        this.fhirBasePath = basePath;
        LOG.info("FHIR R4 base path set to {}", basePath);
    }

    @Given("a known FHIR Patient id {string}")
    public void knownPatientId(String id) {
        this.patientId = id;
        LOG.info("Known FHIR Patient id {}", id);
    }

    @When("I read the Patient resource")
    public void readPatientResource() {
        assertThat(fhirBasePath).as("base path configured").isNotBlank();
        String url = fhirBasePath + "/Patient/" + patientId;
        LOG.info("Reading Patient resource from {}", url);
        resource = JsonPath.from(fetchPatient(patientId));
    }

    @When("I read the FHIR CapabilityStatement")
    public void readCapabilityStatement() {
        assertThat(fhirBasePath).as("base path configured").isNotBlank();
        String url = fhirBasePath + "/metadata";
        LOG.info("Reading CapabilityStatement from {}", url);
        resource = JsonPath.from(fetchCapabilityStatement());
    }

    @Then("the response resourceType is {string}")
    public void resourceTypeIs(String expected) {
        assertThat(resource).as("resource parsed").isNotNull();
        assertThat(resource.getString("resourceType")).isEqualTo(expected);
    }

    @Then("the Patient resource has a logical id")
    public void patientHasLogicalId() {
        assertThat(resource.getString("id")).isEqualTo(patientId);
        assertThat(resource.getString("id")).isNotBlank();
    }

    @Then("the advertised fhirVersion is {string}")
    public void advertisedFhirVersion(String expected) {
        assertThat(resource.getString("fhirVersion")).isEqualTo(expected);
    }

    /**
     * Simulated FHIR Patient read returning a minimal but valid R4 Patient JSON string for the
     * given logical id.
     */
    private String fetchPatient(String id) {
        return "{"
                + "\"resourceType\":\"Patient\","
                + "\"id\":\"" + id + "\","
                + "\"active\":true,"
                + "\"name\":[{\"use\":\"official\",\"family\":\"Doe\",\"given\":[\"Jane\"]}],"
                + "\"gender\":\"female\","
                + "\"birthDate\":\"1985-04-12\""
                + "}";
    }

    /** Simulated FHIR R4 CapabilityStatement advertising fhirVersion 4.0.1. */
    private String fetchCapabilityStatement() {
        return "{"
                + "\"resourceType\":\"CapabilityStatement\","
                + "\"status\":\"active\","
                + "\"fhirVersion\":\"4.0.1\","
                + "\"format\":[\"json\"]"
                + "}";
    }
}
