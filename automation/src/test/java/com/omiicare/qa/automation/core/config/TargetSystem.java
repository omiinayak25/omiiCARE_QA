package com.omiicare.qa.automation.core.config;

/**
 * The external systems the automation platform can target through the adapter layer. Tests
 * reference a {@code TargetSystem}, never a raw URL; the adapter factory resolves the concrete
 * endpoint from configuration, so switching environments is a configuration change only.
 */
public enum TargetSystem {
    LOCAL_OMIICARE("http://localhost:8080/api/v1"),
    OPENMRS("https://demo.openmrs.org/openmrs/ws/rest/v1"),
    OPENEMR("http://localhost:8300/apis/default/api"),
    HAPI_FHIR("https://hapi.fhir.org/baseR4"),
    SMART_HEALTH_IT("https://launch.smarthealthit.org/v/r4/fhir"),
    OPENFDA("https://api.fda.gov"),
    DUMMYJSON("https://dummyjson.com"),
    RESTFUL_BOOKER("https://restful-booker.herokuapp.com");

    private final String defaultBaseUri;

    TargetSystem(String defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }

    public String defaultBaseUri() {
        return defaultBaseUri;
    }

    /** Configuration key used to override this system's base URI. */
    public String baseUriProperty() {
        return "omii.adapter." + name().toLowerCase() + ".baseUri";
    }
}
