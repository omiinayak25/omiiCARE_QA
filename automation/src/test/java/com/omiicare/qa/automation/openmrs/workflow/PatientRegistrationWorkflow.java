package com.omiicare.qa.automation.openmrs.workflow;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.data.model.PatientData;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsHomePage;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsRegistrationPage;

/**
 * Patient-registration business workflow (Workflow layer). Drives the full OpenMRS registration
 * wizard (Demographics → Contact Info → Confirm) from a {@link PatientData} value object. Assumes
 * the session is on the home dashboard.
 */
public final class PatientRegistrationWorkflow extends BaseWorkflow {

    private static final int MAX_WIZARD_STEPS = 8;

    public PatientRegistrationWorkflow(Page page) {
        super(page);
    }

    /** Registers a patient by walking the wizard and confirming. */
    public void registerPatient(PatientData patient) {
        OpenmrsRegistrationPage reg =
                new OpenmrsHomePage(page).openRegisterPatient().waitUntilLoaded();

        // Demographics
        reg.enterName(patient.givenName(), patient.familyName()).next();
        reg.selectGender(patient.gender()).next();
        reg.enterBirthDate(patient.birthDate().getDayOfMonth(), patient.birthDate().getYear()).next();

        // Contact Info (OpenMRS requires at least one address field)
        if (patient.addressLine() != null) {
            reg.enterAddressLine(patient.addressLine());
        }
        if (patient.city() != null) {
            reg.enterCity(patient.city());
        }

        // Walk any remaining optional steps to the Confirm screen, then submit.
        for (int i = 0; i < MAX_WIZARD_STEPS && !reg.isConfirmVisible(); i++) {
            reg.next();
        }
        reg.confirm();
    }
}
