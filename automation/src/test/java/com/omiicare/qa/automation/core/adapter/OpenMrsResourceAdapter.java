package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Adapter for the OpenMRS Reference Application.
 *
 * <p>OpenMRS exposes two distinct API surfaces under a single web context: a legacy REST API at
 * {@code /ws/rest/v1} and a FHIR R4 endpoint at {@code /ws/fhir2/R4}. The configured base URI for
 * {@link TargetSystem#OPENMRS} points at the REST root (e.g.
 * {@code https://demo.openmrs.org/openmrs/ws/rest/v1}); this adapter derives the sibling FHIR root
 * from it so a test can reach either surface from one adapter.
 */
public class OpenMrsResourceAdapter extends HttpResourceAdapter {

    /** Path segment that anchors the OpenMRS web context, used to split REST from FHIR roots. */
    private static final String REST_SUFFIX = "/ws/rest/v1";

    public OpenMrsResourceAdapter(TargetSystem system, FrameworkConfig config) {
        super(system, config);
    }

    /** Convenience constructor binding explicitly to {@link TargetSystem#OPENMRS}. */
    public OpenMrsResourceAdapter(FrameworkConfig config) {
        super(TargetSystem.OPENMRS, config);
    }

    /** Root of the legacy OpenMRS REST API ({@code .../ws/rest/v1}). */
    public String restRoot() {
        return baseUri();
    }

    /**
     * Root of the OpenMRS FHIR R4 API ({@code .../ws/fhir2/R4}), derived from the REST root when the
     * base URI follows the canonical {@code /ws/rest/v1} convention; otherwise falls back to
     * appending the FHIR path to the web context.
     */
    public String fhirRoot() {
        String base = baseUri();
        int idx = base.indexOf(REST_SUFFIX);
        if (idx >= 0) {
            return base.substring(0, idx) + "/ws/fhir2/R4";
        }
        return url("/ws/fhir2/R4");
    }

    /** URL of the FHIR {@code CapabilityStatement} (metadata) used to assert fhirVersion 4.0.1. */
    public String fhirMetadataUrl() {
        return fhirRoot() + "/metadata";
    }

    /** Resolves a REST endpoint path against the REST root (e.g. {@code "/patient"}). */
    public String rest(String path) {
        return joined(restRoot(), path);
    }

    /** Resolves a FHIR resource path against the FHIR root (e.g. {@code "/Patient"}). */
    public String fhir(String path) {
        return joined(fhirRoot(), path);
    }

    private static String joined(String root, String path) {
        boolean rootSlash = root.endsWith("/");
        boolean pathSlash = path.startsWith("/");
        if (rootSlash && pathSlash) {
            return root + path.substring(1);
        }
        if (!rootSlash && !pathSlash) {
            return root + "/" + path;
        }
        return root + path;
    }
}
