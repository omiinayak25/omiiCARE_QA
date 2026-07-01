package com.omiicare.qa.automation.openmrs.workflow;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.openmrs.component.NavigationComponent;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsFindPatientPage;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsHomePage;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsRegistrationPage;

/**
 * Navigation business workflow (Workflow layer): move between the OpenMRS home apps.
 */
public final class NavigationWorkflow extends BaseWorkflow {

    public NavigationWorkflow(Page page) {
        super(page);
    }

    public OpenmrsRegistrationPage goToRegisterPatient() {
        return new OpenmrsHomePage(page).openRegisterPatient();
    }

    public OpenmrsFindPatientPage goToFindPatient() {
        return new OpenmrsHomePage(page).openFindPatient();
    }

    /** Returns to the home dashboard via the breadcrumb. */
    public void goHome() {
        new NavigationComponent(page).goHome();
    }
}
