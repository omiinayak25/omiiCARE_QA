package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.TargetSystem;

/**
 * Common interface every target system implements. Tests call the interface, not
 * the system, so a new target is added by writing a new adapter and existing
 * tests remain unchanged (Adapter pattern).
 */
public interface ResourceAdapter {

    /** The system this adapter targets. */
    TargetSystem system();

    /** Fully-resolved base URI for the target (from configuration). */
    String baseUri();

    /** Resolves an endpoint path against the base URI. */
    default String url(String path) {
        String base = baseUri();
        if (base.endsWith("/") && path.startsWith("/")) {
            return base + path.substring(1);
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }
}
