package com.omiicare.qa.automation.selenium.pages;

import com.omiicare.qa.automation.selenium.SeleniumConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the OpenMRS Reference Application home dashboard, reached after a successful
 * login. Exposes the primary app tiles (Register a Patient, Find Patient Record) and the
 * logged-in user affordance used to confirm authentication.
 *
 * <p>Verified anchors: {@code a[href*=registerPatient.page]} (Register a patient) and
 * {@code a[href*='app=coreapps.findPatient']} (Find Patient Record).
 */
public final class HomePage extends BasePage {

    private static final By REGISTER_PATIENT_TILE = By.cssSelector("a[href*=\"registerPatient.page\"]");
    private static final By FIND_PATIENT_TILE = By.cssSelector("a[href*=\"app=coreapps.findPatient\"]");
    // The header shows the authenticated user; presence confirms a session is established.
    private static final By LOGGED_IN_USER = By.id("logged-in-as");
    private static final By LOGOUT_LINK = By.cssSelector("a[href*=\"logout\"]");

    public HomePage(WebDriver driver, SeleniumConfig config) {
        super(driver, config);
    }

    /**
     * True once the dashboard has rendered for an authenticated session. Considered loaded when
     * either home tile is visible (the URL also lands on the home app).
     */
    public boolean isLoaded() {
        return isDisplayed(REGISTER_PATIENT_TILE) || isDisplayed(FIND_PATIENT_TILE);
    }

    /** Waits for the dashboard to finish loading and returns this page for chaining. */
    public HomePage waitUntilLoaded() {
        waitForPresence(REGISTER_PATIENT_TILE);
        return this;
    }

    /** True when an authenticated session is reflected in the header. */
    public boolean isUserLoggedIn() {
        return isDisplayed(LOGGED_IN_USER) || isDisplayed(LOGOUT_LINK) || isLoaded();
    }

    /** Opens the patient registration workflow. */
    public RegistrationPage openRegisterPatient() {
        click(REGISTER_PATIENT_TILE);
        return new RegistrationPage(driver, config);
    }

    /** Opens the find-patient workflow. */
    public FindPatientPage openFindPatient() {
        click(FIND_PATIENT_TILE);
        return new FindPatientPage(driver, config);
    }
}
