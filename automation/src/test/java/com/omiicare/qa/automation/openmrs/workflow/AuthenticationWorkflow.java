package com.omiicare.qa.automation.openmrs.workflow;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.openmrs.component.HeaderComponent;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsHomePage;
import com.omiicare.qa.automation.ui.openmrs.OpenmrsLoginPage;

/**
 * Authentication business workflow (Workflow layer): login at a session location, login as the
 * configured demo admin, attempt a (possibly failing) login, and logout.
 */
public final class AuthenticationWorkflow extends BaseWorkflow {

    public AuthenticationWorkflow(Page page) {
        super(page);
    }

    /** Full happy-path login; returns the authenticated home dashboard. */
    public OpenmrsHomePage loginAs(String username, String password, String locationId) {
        return new OpenmrsLoginPage(page).loginAs(baseUrl, username, password, locationId);
    }

    /** Logs in as the configured demo administrator (defaults: admin / Admin123 / Registration Desk). */
    public OpenmrsHomePage loginAsAdmin() {
        FrameworkConfig config = FrameworkConfig.get();
        return loginAs(
                config.get("omii.openmrs.username", "admin"),
                config.get("omii.openmrs.password", "Admin123"),
                config.get("omii.openmrs.location", "Registration Desk"));
    }

    /**
     * Attempts a login without asserting success — used by negative tests to exercise the
     * invalid-credentials path. Leaves the browser on whatever page the app returns.
     */
    public void attemptLogin(String username, String password, String locationId) {
        new OpenmrsLoginPage(page)
                .open(baseUrl)
                .chooseLoginLocation(locationId)
                .enterCredentials(username, password)
                .submit();
    }

    /** Logs out of the current session via the header. */
    public void logout() {
        new HeaderComponent(page).logout();
    }
}
