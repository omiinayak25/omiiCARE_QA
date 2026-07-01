package com.omiicare.qa.automation.openmrs.service;

import com.omiicare.qa.automation.api.rest.OpenMrsRestClient;
import com.omiicare.qa.automation.api.rest.SessionInfo;

/**
 * Authentication service (Service layer — Facade over {@link OpenMrsRestClient}). Exposes
 * intention-revealing auth operations with no UI dependency; reused by API tests and by UI
 * workflows that need a pre-authenticated session or an auth precondition check.
 */
public final class AuthenticationService {

    private final OpenMrsRestClient client;

    public AuthenticationService() {
        this(new OpenMrsRestClient());
    }

    public AuthenticationService(OpenMrsRestClient client) {
        this.client = client;
    }

    /** Opens (and returns) the current REST session for the configured credentials. */
    public SessionInfo openSession() {
        return client.openSession();
    }

    /** @return true when the configured credentials authenticate successfully. */
    public boolean isAuthenticated() {
        return client.openSession().authenticated();
    }
}
