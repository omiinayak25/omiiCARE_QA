package com.omiicare.qa.automation.openmrs.service;

import com.omiicare.qa.automation.fhir.FhirClient;
import io.restassured.response.Response;

/**
 * FHIR R4 service (Service layer — Facade over {@link FhirClient}). No UI dependency. Switching
 * FHIR servers (OpenMRS RefApp, HAPI, SMART Health IT) is a configuration change only.
 */
public final class FhirService {

    private final FhirClient client;

    public FhirService() {
        this(new FhirClient());
    }

    public FhirService(FhirClient client) {
        this.client = client;
    }

    /** {@code GET /metadata} — the server CapabilityStatement. */
    public Response capabilityStatement() {
        return client.metadata();
    }

    /** {@code GET /Patient/{id}}. */
    public Response readPatient(String id) {
        return client.read("Patient", id);
    }

    public String baseUri() {
        return client.baseUri();
    }
}
