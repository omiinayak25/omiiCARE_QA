package com.omiicare.qa.automation.openmrs.service;

import com.omiicare.qa.automation.api.rest.EncounterSummary;
import com.omiicare.qa.automation.api.rest.OpenMrsRestClient;
import com.omiicare.qa.automation.api.rest.PatientSummary;
import java.util.List;

/**
 * OpenMRS patient REST service (Service layer — Facade over {@link OpenMrsRestClient}). No UI
 * dependency. Reused by API tests and by UI workflows that verify UI actions against the backend.
 */
public final class OpenMrsPatientService {

    private final OpenMrsRestClient client;

    public OpenMrsPatientService() {
        this(new OpenMrsRestClient());
    }

    public OpenMrsPatientService(OpenMrsRestClient client) {
        this.client = client;
    }

    public List<PatientSummary> search(String query) {
        return client.searchPatients(query);
    }

    public PatientSummary get(String uuid) {
        return client.getPatient(uuid);
    }

    public List<EncounterSummary> encounters(String patientUuid) {
        return client.getEncountersForPatient(patientUuid);
    }
}
