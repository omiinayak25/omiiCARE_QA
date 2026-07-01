package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Adapter for the omiiCARE platform under test.
 *
 * <p>omiiCARE exposes a versioned REST API whose base URI (e.g.
 * {@code http://localhost:8080/api/v1}) is resolved for {@link TargetSystem#LOCAL_OMIICARE}. This
 * adapter centralises common path conventions (auth, versioned resources) so tests do not embed raw
 * URLs.
 */
public class OmiiCareResourceAdapter extends HttpResourceAdapter {

    public OmiiCareResourceAdapter(TargetSystem system, FrameworkConfig config) {
        super(system, config);
    }

    /** Convenience constructor binding explicitly to {@link TargetSystem#LOCAL_OMIICARE}. */
    public OmiiCareResourceAdapter(FrameworkConfig config) {
        super(TargetSystem.LOCAL_OMIICARE, config);
    }

    /** Root of the versioned omiiCARE REST API. */
    public String apiRoot() {
        return baseUri();
    }

    /** Resolves a REST endpoint path against the API root (e.g. {@code "/patients"}). */
    public String api(String path) {
        return url(path);
    }

    /** Authentication endpoint used to obtain a session/bearer token. */
    public String authUrl() {
        return url("/auth/login");
    }
}
