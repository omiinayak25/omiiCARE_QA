package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the OpenMRS Reference Application patient-registration wizard. The RefApp
 * registration form is a multi-step questionnaire whose first step captures the patient's name. The
 * field ids are stable across the standard distribution ({@code #givenName}, {@code #familyName})
 * and the wizard advances via the {@code #next} / submit control.
 *
 * <p>This object models the first (demographics) step plus the navigation primitives; richer steps
 * can be layered on by subsequent suites without changing this contract.
 */
public final class OpenmrsRegistrationPage extends PwBasePage {

    private static final Logger LOG = LoggerFactory.getLogger(OpenmrsRegistrationPage.class);

    private static final String GIVEN_NAME = "#givenName";
    private static final String FAMILY_NAME = "#familyName";
    private static final String NEXT_BUTTON = "#next, input[value='Next'], button:has-text('Next')";
    private static final String CONFIRM_BUTTON =
            "#submit, input[value='Confirm'], button:has-text('Confirm')";
    private static final String FORM_ROOT = "form, #registrationForm, .form-wrapper";

    /**
     * @param page the live Playwright page
     */
    public OpenmrsRegistrationPage(Page page) {
        super(page);
    }

    /**
     * Waits until the registration form has rendered. The name field, when present, is the strongest
     * signal; otherwise the form container is used as a fallback for theme variations.
     *
     * @return this page object
     */
    public OpenmrsRegistrationPage waitUntilLoaded() {
        page.waitForSelector(
                GIVEN_NAME + ", " + FORM_ROOT,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        LOG.debug("OpenMRS registration form loaded at {}", currentUrl());
        return this;
    }

    /** @return {@code true} if the given-name field is visible. */
    public boolean isLoaded() {
        return isVisible(GIVEN_NAME);
    }

    /**
     * Enters the patient's given (first) name.
     *
     * @param givenName the given name
     * @return this page object
     */
    public OpenmrsRegistrationPage enterGivenName(String givenName) {
        type(GIVEN_NAME, givenName);
        return this;
    }

    /**
     * Enters the patient's family (last) name.
     *
     * @param familyName the family name
     * @return this page object
     */
    public OpenmrsRegistrationPage enterFamilyName(String familyName) {
        type(FAMILY_NAME, familyName);
        return this;
    }

    /**
     * Enters both name components in one call.
     *
     * @param givenName the given name
     * @param familyName the family name
     * @return this page object
     */
    public OpenmrsRegistrationPage enterName(String givenName, String familyName) {
        return enterGivenName(givenName).enterFamilyName(familyName);
    }

    /**
     * Advances the wizard to the next step.
     *
     * @return this page object
     */
    public OpenmrsRegistrationPage next() {
        click(NEXT_BUTTON);
        return this;
    }

    /**
     * Confirms / submits the registration.
     *
     * @return this page object
     */
    public OpenmrsRegistrationPage confirm() {
        click(CONFIRM_BUTTON);
        return this;
    }
}
