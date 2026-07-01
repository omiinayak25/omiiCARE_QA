package com.omiicare.qa.automation.openmrs.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;

/**
 * Fluent REST/FHIR response assertions (Assertion layer) over a RestAssured {@link Response}.
 * Complements the dedicated {@code fhir.FhirAssertions} and {@code db.DbAssertions} classes.
 */
public final class ApiAssertions {

    private final Response response;

    private ApiAssertions(Response response) {
        this.response = response;
    }

    public static ApiAssertions on(Response response) {
        return new ApiAssertions(response);
    }

    public ApiAssertions hasStatus(int expected) {
        assertThat(response.statusCode()).as("HTTP status").isEqualTo(expected);
        return this;
    }

    public ApiAssertions statusIsOneOf(int... allowed) {
        int actual = response.statusCode();
        boolean match = false;
        for (int code : allowed) {
            if (code == actual) {
                match = true;
                break;
            }
        }
        assertThat(match).as("HTTP status %d in allowed set", actual).isTrue();
        return this;
    }

    public ApiAssertions jsonPathEquals(String path, Object expected) {
        Object actual = response.jsonPath().get(path);
        assertThat(actual).as("json path '%s'", path).isEqualTo(expected);
        return this;
    }

    public ApiAssertions bodyContains(String text) {
        assertThat(response.getBody().asString()).as("response body").contains(text);
        return this;
    }
}
