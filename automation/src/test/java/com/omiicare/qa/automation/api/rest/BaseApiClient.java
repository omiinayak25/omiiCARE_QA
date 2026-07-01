package com.omiicare.qa.automation.api.rest;

import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Foundation for all REST API clients in the platform. Owns a reusable {@link RequestSpecification}
 * built from {@link ApiConfig}: base URI, JSON content negotiation, connect/socket timeouts, and
 * request/response logging. Concrete clients (e.g. {@link OpenMrsRestClient}) layer authentication
 * and endpoint-specific behavior on top of {@link #request()}.
 *
 * <p>Instances are lightweight and thread-confined; create one per logical session rather than
 * sharing across threads.
 */
public class BaseApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(BaseApiClient.class);

    private final ApiConfig config;
    private final RequestSpecification baseSpec;

    /**
     * Creates a client using the framework-resolved configuration.
     */
    public BaseApiClient() {
        this(ApiConfig.fromFramework());
    }

    /**
     * Creates a client bound to an explicit configuration.
     *
     * @param config the resolved API configuration; must not be {@code null}
     */
    public BaseApiClient(ApiConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("ApiConfig must not be null");
        }
        this.config = config;
        this.baseSpec = buildBaseSpec(config);
        LOG.debug("Initialized {} against {}", getClass().getSimpleName(), config.baseUri());
    }

    private static RequestSpecification buildBaseSpec(ApiConfig config) {
        RestAssuredConfig raConfig =
                RestAssuredConfig.config()
                        .httpClient(
                                HttpClientConfig.httpClientConfig()
                                        .setParam("http.connection.timeout", config.timeoutMs())
                                        .setParam("http.socket.timeout", config.timeoutMs()));

        return new RequestSpecBuilder()
                .setBaseUri(config.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(raConfig)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    /**
     * Returns a fresh request specification seeded from the shared base spec. Each call yields an
     * independent builder, so callers may add headers, auth, query params, or bodies without
     * affecting other requests.
     *
     * @return a new {@link RequestSpecification} ready for method-chaining
     */
    protected RequestSpecification request() {
        return given().spec(baseSpec);
    }

    /**
     * @return the configuration backing this client
     */
    public ApiConfig config() {
        return config;
    }

    /**
     * @return the resolved base URI for this client
     */
    public String baseUri() {
        return config.baseUri();
    }
}
