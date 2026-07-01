package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the OpenMRS Reference Application home dashboard rendered after a successful
 * login. The home screen presents a grid of application tiles; this object exposes the two tiles
 * the test suite cares about and the navigation away from them.
 *
 * <p>Stable selectors used: the registration tile {@code a[href*=registerPatient.page]} and the
 * find-patient tile {@code a[href*='app=coreapps.findPatient']}.
 */
public final class OpenmrsHomePage extends PwBasePage {

    private static final Logger LOG = LoggerFactory.getLogger(OpenmrsHomePage.class);

    private static final String REGISTER_PATIENT_TILE = "a[href*=registerPatient.page]";
    private static final String FIND_PATIENT_TILE = "a[href*='app=coreapps.findPatient']";

    /**
     * @param page the live Playwright page
     */
    public OpenmrsHomePage(Page page) {
        super(page);
    }

    /**
     * Waits until at least one of the recognizable home tiles is visible, confirming the dashboard
     * has rendered.
     *
     * @return this page object
     */
    public OpenmrsHomePage waitUntilLoaded() {
        // Either tile being present is sufficient evidence the home screen loaded.
        page.waitForSelector(
                REGISTER_PATIENT_TILE + ", " + FIND_PATIENT_TILE,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        LOG.debug("OpenMRS home dashboard loaded at {}", currentUrl());
        return this;
    }

    /** @return {@code true} if the registration tile is visible. */
    public boolean isRegisterPatientTileVisible() {
        return isVisible(REGISTER_PATIENT_TILE);
    }

    /** @return {@code true} if the find-patient tile is visible. */
    public boolean isFindPatientTileVisible() {
        return isVisible(FIND_PATIENT_TILE);
    }

    /**
     * Clicks the "Register a patient" tile.
     *
     * @return a {@link OpenmrsRegistrationPage}
     */
    public OpenmrsRegistrationPage openRegisterPatient() {
        click(REGISTER_PATIENT_TILE);
        OpenmrsRegistrationPage registration = new OpenmrsRegistrationPage(page);
        registration.waitUntilLoaded();
        return registration;
    }

    /**
     * Clicks the "Find Patient Record" tile.
     *
     * @return a {@link OpenmrsFindPatientPage}
     */
    public OpenmrsFindPatientPage openFindPatient() {
        click(FIND_PATIENT_TILE);
        OpenmrsFindPatientPage find = new OpenmrsFindPatientPage(page);
        find.waitUntilLoaded();
        return find;
    }

    /** @return the count of application tiles currently rendered on the dashboard. */
    public int tileCount() {
        Locator tiles = page.locator("a[href*='.page'], a[href*='app=']");
        return tiles.count();
    }
}
