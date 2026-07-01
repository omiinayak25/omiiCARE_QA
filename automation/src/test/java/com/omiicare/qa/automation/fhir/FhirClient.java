package com.omiicare.qa.automation.fhir;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.omiicare.qa.automation.core.config.FrameworkConfig;

/**
 * Thin, configuration-driven RestAssured client for FHIR R4 REST endpoints.
 *
 * <p>All requests negotiate the FHIR JSON media type ({@code application/fhir+json}) for both the
 * {@code Accept} and {@code Content-Type} headers, as required by the FHIR R4 HTTP specification.
 * The base URI is resolved through {@link FrameworkConfig} so no endpoint is hardcoded in tests;
 * switching target servers (OpenMRS RefApp, HAPI public server, SMART Health IT, ...) is purely a
 * configuration change.
 *
 * <p>This class performs no assertions — it only issues HTTP calls and returns the raw
 * {@link Response}. Validation is the responsibility of {@link FhirAssertions}.
 */
public final class FhirClient {

    /** FHIR R4 JSON media type used for both request and response negotiation. */
    public static final String FHIR_JSON = "application/fhir+json";

    /** Configuration key for the FHIR base URI (e.g. {@code https://host/ws/fhir2/R4}). */
    public static final String BASE_URI_KEY = "omii.fhir.baseUri";

    /** Default base URI: the OpenMRS Reference Application public demo FHIR R4 endpoint. */
    public static final String DEFAULT_BASE_URI = "https://o2.openmrs.org/openmrs/ws/fhir2/R4";

    private final String baseUri;

    /** Creates a client whose base URI is resolved from {@link FrameworkConfig}. */
    public FhirClient() {
        this(FrameworkConfig.get().get(BASE_URI_KEY, DEFAULT_BASE_URI));
    }

    /**
     * Creates a client targeting an explicit base URI. Useful for parameterized multi-system tests.
     *
     * @param baseUri the FHIR R4 base URI, without a trailing slash
     */
    public FhirClient(String baseUri) {
        if (baseUri == null || baseUri.isBlank()) {
            throw new IllegalArgumentException("FHIR base URI must not be blank");
        }
        this.baseUri = baseUri.endsWith("/") ? baseUri.substring(0, baseUri.length() - 1) : baseUri;
    }

    /** @return the resolved, normalized base URI this client targets. */
    public String baseUri() {
        return baseUri;
    }

    /**
     * Builds a request specification pre-configured with the FHIR base URI and JSON media types.
     * Relaxed HTTPS validation is enabled so self-signed demo servers do not break the suite.
     *
     * @return a fresh {@link RequestSpecification} ready for a FHIR call
     */
    public RequestSpecification request() {
        RestAssuredConfig config =
                RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation());
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setConfig(config)
                .setAccept(FHIR_JSON)
                .setContentType(FHIR_JSON)
                .build();
    }

    /**
     * Reads the server's {@code CapabilityStatement} from the {@code /metadata} endpoint.
     *
     * @return the raw {@link Response}
     */
    public Response metadata() {
        return RestAssured.given()
                .spec(request())
                .when()
                .get("/metadata")
                .thenReturn();
    }

    /**
     * Performs a FHIR {@code read} interaction: {@code GET [base]/[type]/[id]}.
     *
     * @param resourceType the FHIR resource type, e.g. {@code Patient}
     * @param id the logical id of the resource instance
     * @return the raw {@link Response}
     */
    public Response read(String resourceType, String id) {
        return RestAssured.given()
                .spec(request())
                .when()
                .get("/{type}/{id}", resourceType, id)
                .thenReturn();
    }

    /**
     * Performs a FHIR {@code search} interaction: {@code GET [base]/[type]?[params]}.
     *
     * @param resourceType the FHIR resource type to search, e.g. {@code Patient}
     * @param queryParams alternating name/value query parameter pairs (may be empty)
     * @return the raw {@link Response}
     */
    public Response search(String resourceType, Object... queryParams) {
        RequestSpecification spec = RestAssured.given().spec(request());
        if (queryParams != null) {
            if (queryParams.length % 2 != 0) {
                throw new IllegalArgumentException(
                        "queryParams must be alternating name/value pairs");
            }
            for (int i = 0; i < queryParams.length; i += 2) {
                spec = spec.queryParam(String.valueOf(queryParams[i]), queryParams[i + 1]);
            }
        }
        return spec.when().get("/{type}", resourceType).thenReturn();
    }

    /** @return the FHIR JSON content type as a RestAssured {@link ContentType} helper value. */
    public static String fhirJsonMediaType() {
        return FHIR_JSON;
    }
}
