package com.omiicare.qa.automation.openmrs.tests;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.data.TestDataFactory;
import com.omiicare.qa.automation.data.model.PatientData;
import com.omiicare.qa.automation.openmrs.assertion.OpenMrsAssertions;
import com.omiicare.qa.automation.openmrs.workflow.AuthenticationWorkflow;
import com.omiicare.qa.automation.openmrs.workflow.PatientRegistrationWorkflow;
import com.omiicare.qa.automation.ui.openmrs.PlaywrightFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Reference example of the Layered Enterprise Test Automation Architecture end-to-end. Notice the
 * test contains ONLY workflow calls + assertion-layer calls + data-factory calls — no locators, no
 * business logic, no test-data construction. Tagged {@code ui-e2e} (needs a real browser + the live
 * OpenMRS SUT); excluded from the default build, run via {@code -Pe2e}.
 *
 * <pre>
 *   Test  ->  Workflow  ->  Page/Component  ->  Service
 *                       ->  Data (factory/builder)
 *                       ->  Assertion
 * </pre>
 */
@Tag("ui-e2e")
class LayeredSmokeE2ETest {

    private static PlaywrightFactory factory;
    private static Page page;

    @BeforeAll
    static void openBrowser() {
        factory = new PlaywrightFactory();
        page = factory.createPage();
    }

    @AfterAll
    static void closeBrowser() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    @Tag("smoke")
    void adminCanLogInAndReachHomeDashboard() {
        new AuthenticationWorkflow(page).loginAsAdmin();
        OpenMrsAssertions.login(page).isOnHomeDashboard();
    }

    @Test
    @Tag("regression")
    void canRegisterAPatientThroughTheWorkflow() {
        new AuthenticationWorkflow(page).loginAsAdmin();
        PatientData patient = TestDataFactory.randomPatient();
        new PatientRegistrationWorkflow(page).registerPatient(patient);
        OpenMrsAssertions.patient(page).wasRegistered(patient.familyName());
    }
}
