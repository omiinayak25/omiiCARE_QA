package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * OpenMRS top navigation bar (Page Component layer): user menu, session location, and the Logout
 * link (which lives inside a collapsible navbar). Reused by every authenticated page/workflow.
 */
public final class HeaderComponent extends BaseComponent {

    private static final String LOGOUT = "a[href*=\"logout.action\"]";
    private static final String TOGGLE = "button:has-text(\"Toggle navigation\")";

    public HeaderComponent(Page page) {
        super(page);
    }

    /** @return true if a Logout control exists in the DOM (may be collapsed). */
    public boolean isLogoutPresent() {
        return within(LOGOUT).count() > 0;
    }

    /** Reveals the collapsible navbar if needed, then clicks Logout. */
    public void logout() {
        Locator logout = within(LOGOUT).first();
        if (!logout.isVisible()) {
            Locator toggle = within(TOGGLE).first();
            if (toggle.count() > 0 && toggle.isVisible()) {
                toggle.click();
            }
        }
        logout.click();
    }
}
