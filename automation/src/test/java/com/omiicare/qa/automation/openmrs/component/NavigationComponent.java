package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Page;

/**
 * OpenMRS home app-tile navigation + breadcrumb (Page Component layer). Encapsulates the
 * home-dashboard entry points so pages/workflows don't repeat these selectors.
 */
public final class NavigationComponent extends BaseComponent {

    private static final String REGISTER_TILE = "a[href*=\"registerPatient.page\"]";
    private static final String FIND_PATIENT_TILE = "a[href*=\"app=coreapps.findPatient\"]";
    private static final String HOME_BREADCRUMB = "#breadcrumbs a";

    public NavigationComponent(Page page) {
        super(page);
    }

    /** @return true when the home dashboard tiles are present. */
    public boolean isHomeLoaded() {
        return within(REGISTER_TILE).count() > 0;
    }

    public void openRegisterPatient() {
        within(REGISTER_TILE).first().click();
    }

    public void openFindPatient() {
        within(FIND_PATIENT_TILE).first().click();
    }

    /** Navigates back to the home dashboard via the breadcrumb home icon. */
    public void goHome() {
        within(HOME_BREADCRUMB).first().click();
    }
}
