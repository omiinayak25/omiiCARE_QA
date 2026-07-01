package com.omiicare.qa.automation.fhir;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;

/**
 * Fluent, AssertJ-backed assertions for FHIR R4 resources and {@code OperationOutcome}s.
 *
 * <p>The class wraps a parsed FHIR JSON payload and exposes chainable checks for the structural
 * invariants common to every R4 interaction: the {@code resourceType} discriminator, the published
 * FHIR version ({@code 4.0.1} from a {@code CapabilityStatement}), presence of required fields, and
 * detection / inspection of {@code OperationOutcome} error envelopes.
 *
 * <p>Parsing is delegated to RestAssured's {@link JsonPath} (no third-party JSON library), keeping
 * the unit self-contained and dependency-light.
 *
 * <pre>{@code
 * FhirAssertions.assertThatResource(response)
 *     .hasResourceType("Patient")
 *     .hasRequiredFields("id")
 *     .isNotOperationOutcome();
 * }</pre>
 */
public final class FhirAssertions {

    /** The single FHIR version this framework targets (R4). */
    public static final String FHIR_R4_VERSION = "4.0.1";

    private final JsonPath json;

    private FhirAssertions(JsonPath json) {
        this.json = json;
    }

    /**
     * Entry point: wraps a RestAssured {@link Response} body for assertion.
     *
     * @param response a non-null HTTP response carrying a FHIR JSON body
     * @return a new assertion handle
     */
    public static FhirAssertions assertThatResource(Response response) {
        assertThat(response).as("FHIR response").isNotNull();
        return new FhirAssertions(response.jsonPath());
    }

    /**
     * Entry point: wraps a raw FHIR JSON string for assertion.
     *
     * @param rawJson a non-null, non-blank FHIR JSON document
     * @return a new assertion handle
     */
    public static FhirAssertions assertThatJson(String rawJson) {
        assertThat(rawJson).as("FHIR JSON payload").isNotNull().isNotBlank();
        return new FhirAssertions(new JsonPath(rawJson));
    }

    /** @return the underlying {@link JsonPath} for advanced, ad-hoc inspection. */
    public JsonPath json() {
        return json;
    }

    /**
     * Asserts the {@code resourceType} discriminator equals the expected value.
     *
     * @param expected the expected FHIR resource type, e.g. {@code Patient}
     * @return this handle for chaining
     */
    public FhirAssertions hasResourceType(String expected) {
        assertThat(json.getString("resourceType"))
                .as("resourceType")
                .isEqualTo(expected);
        return this;
    }

    /**
     * Asserts the document is a {@code CapabilityStatement} publishing FHIR version {@code 4.0.1}.
     *
     * @return this handle for chaining
     */
    public FhirAssertions hasFhirVersionR4() {
        hasResourceType("CapabilityStatement");
        assertThat(json.getString("fhirVersion"))
                .as("CapabilityStatement.fhirVersion")
                .isEqualTo(FHIR_R4_VERSION);
        return this;
    }

    /**
     * Asserts that every named field is present and non-null in the resource.
     *
     * @param fieldPaths one or more JsonPath expressions (e.g. {@code id}, {@code name[0].family})
     * @return this handle for chaining
     */
    public FhirAssertions hasRequiredFields(String... fieldPaths) {
        assertThat(fieldPaths).as("required field list").isNotEmpty();
        for (String path : fieldPaths) {
            assertThat((Object) json.get(path))
                    .as("required FHIR field '%s'", path)
                    .isNotNull();
        }
        return this;
    }

    /**
     * Asserts the resource carries the supplied logical id.
     *
     * @param id the expected {@code id} value
     * @return this handle for chaining
     */
    public FhirAssertions hasId(String id) {
        assertThat(json.getString("id")).as("Resource.id").isEqualTo(id);
        return this;
    }

    /** @return {@code true} if the wrapped document is a FHIR {@code OperationOutcome}. */
    public boolean isOperationOutcome() {
        return "OperationOutcome".equals(json.getString("resourceType"));
    }

    /**
     * Asserts the document IS an {@code OperationOutcome} carrying at least one issue.
     *
     * @return this handle for chaining
     */
    public FhirAssertions isOperationOutcomeWithIssues() {
        hasResourceType("OperationOutcome");
        List<Object> issues = json.getList("issue");
        assertThat(issues).as("OperationOutcome.issue").isNotNull().isNotEmpty();
        return this;
    }

    /**
     * Asserts the document is NOT an {@code OperationOutcome} (i.e. the call succeeded).
     *
     * @return this handle for chaining
     */
    public FhirAssertions isNotOperationOutcome() {
        assertThat(json.getString("resourceType"))
                .as("resourceType (expected a resource, not an OperationOutcome)")
                .isNotEqualTo("OperationOutcome");
        return this;
    }

    /**
     * Asserts that an {@code OperationOutcome} contains an issue with the given severity.
     *
     * @param severity one of {@code fatal}, {@code error}, {@code warning}, {@code information}
     * @return this handle for chaining
     */
    public FhirAssertions hasIssueSeverity(String severity) {
        isOperationOutcomeWithIssues();
        List<String> severities = json.getList("issue.severity");
        assertThat(severities)
                .as("OperationOutcome.issue.severity")
                .contains(severity);
        return this;
    }
}
