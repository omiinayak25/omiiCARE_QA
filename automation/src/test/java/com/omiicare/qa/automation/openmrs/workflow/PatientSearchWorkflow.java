package com.omiicare.qa.automation.openmrs.workflow;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsFindPatientPage;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsHomePage;

/**
 * Patient-search business workflow (Workflow layer): open Find Patient Record and search. Assumes
 * the session is on the home dashboard.
 */
public final class PatientSearchWorkflow extends BaseWorkflow {

    public PatientSearchWorkflow(Page page) {
        super(page);
    }

    /** Opens Find Patient Record and searches for the query; returns the results page. */
    public OpenmrsFindPatientPage findPatient(String query) {
        OpenmrsFindPatientPage find =
                new OpenmrsHomePage(page).openFindPatient().waitUntilLoaded();
        return find.search(query);
    }
}
