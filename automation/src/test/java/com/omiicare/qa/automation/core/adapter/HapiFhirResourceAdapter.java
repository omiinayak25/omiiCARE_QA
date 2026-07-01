package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Adapter for the public HAPI FHIR test server.
 *
 * <p>HAPI is a pure FHIR R4 server: the configured base URI for {@link TargetSystem#HAPI_FHIR}
 * (e.g. {@code https://hapi.fhir.org/baseR4}) is itself the FHIR root, so there is no separate REST
 * surface. This adapter adds FHIR-aware helpers for resource paths and the conformance metadata.
 */
public class HapiFhirResourceAdapter extends HttpResourceAdapter {

    public HapiFhirResourceAdapter(TargetSystem system, FrameworkConfig config) {
        super(system, config);
    }

    /** Convenience constructor binding explicitly to {@link TargetSystem#HAPI_FHIR}. */
    public HapiFhirResourceAdapter(FrameworkConfig config) {
        super(TargetSystem.HAPI_FHIR, config);
    }

    /** The FHIR R4 base, which for HAPI equals the configured base URI. */
    public String fhirRoot() {
        return baseUri();
    }

    /** URL of the FHIR {@code CapabilityStatement} (metadata). */
    public String fhirMetadataUrl() {
        return url("/metadata");
    }

    /** Resolves a FHIR resource path against the FHIR root (e.g. {@code "/Patient"}). */
    public String fhir(String path) {
        return url(path);
    }

    /** URL for reading a single resource instance by type and logical id. */
    public String resource(String resourceType, String id) {
        return url("/" + resourceType + "/" + id);
    }
}
