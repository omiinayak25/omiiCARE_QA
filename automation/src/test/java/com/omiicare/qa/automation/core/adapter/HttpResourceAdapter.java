package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Default HTTP adapter: resolves the base URI for any {@link TargetSystem} from
 * configuration. Suitable for REST/FHIR targets reached over HTTP; specialized
 * adapters can extend this when a system needs bespoke auth or pathing.
 */
public class HttpResourceAdapter implements ResourceAdapter {

    private final TargetSystem system;
    private final String baseUri;

    public HttpResourceAdapter(TargetSystem system, FrameworkConfig config) {
        this.system = system;
        this.baseUri = config.baseUri(system);
    }

    @Override
    public TargetSystem system() {
        return system;
    }

    @Override
    public String baseUri() {
        return baseUri;
    }
}
