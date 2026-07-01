package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Adapter for OpenEMR.
 *
 * <p>OpenEMR serves a standard REST API under {@code /apis/default/api} and a separate FHIR R4 API
 * under {@code /apis/default/fhir}. The configured base URI for {@link TargetSystem#OPENEMR} points
 * at the REST root; this adapter derives the sibling FHIR root and exposes the OAuth2 token endpoint,
 * since OpenEMR protects both surfaces with bearer tokens.
 */
public class OpenEmrResourceAdapter extends HttpResourceAdapter {

    private static final String REST_SUFFIX = "/apis/default/api";

    public OpenEmrResourceAdapter(TargetSystem system, FrameworkConfig config) {
        super(system, config);
    }

    /** Convenience constructor binding explicitly to {@link TargetSystem#OPENEMR}. */
    public OpenEmrResourceAdapter(FrameworkConfig config) {
        super(TargetSystem.OPENEMR, config);
    }

    /** Root of the OpenEMR standard REST API ({@code .../apis/default/api}). */
    public String restRoot() {
        return baseUri();
    }

    /** Root of the OpenEMR FHIR R4 API ({@code .../apis/default/fhir}), derived from the REST root. */
    public String fhirRoot() {
        String base = baseUri();
        int idx = base.indexOf(REST_SUFFIX);
        if (idx >= 0) {
            return base.substring(0, idx) + "/apis/default/fhir";
        }
        return url("/apis/default/fhir");
    }

    /**
     * OAuth2 token endpoint ({@code .../oauth2/default/token}). OpenEMR issues bearer tokens from the
     * web-context root (the segment before {@code /apis}); derived here so credential flows need no
     * extra configuration.
     */
    public String tokenUrl() {
        String base = baseUri();
        int idx = base.indexOf(REST_SUFFIX);
        String contextRoot = idx >= 0 ? base.substring(0, idx) : base;
        return contextRoot + "/oauth2/default/token";
    }

    /** Resolves a REST endpoint path against the REST root. */
    public String rest(String path) {
        return joined(restRoot(), path);
    }

    /** Resolves a FHIR resource path against the FHIR root. */
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
