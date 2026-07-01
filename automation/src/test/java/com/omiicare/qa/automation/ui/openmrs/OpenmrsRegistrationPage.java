package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the OpenMRS Reference Application patient-registration wizard (Page Object layer).
 * The RefApp registration form is a multi-step questionnaire: Demographics (name, gender, birthdate)
 * → Contact Info (address, phone) → Confirm. Field selectors below are the verified RefApp
 * selectors ({@code input[name="givenName"]}, {@code select[name="gender"]},
 * {@code #birthdateDay-field}, {@code #address1}, {@code #next-button}, {@code #submit}).
 *
 * <p>Locators + UI interactions only — no assertions, no cross-page business logic. The complete
 * registration business process lives in {@code PatientRegistrationWorkflow}.
 */
public final class OpenmrsRegistrationPage extends PwBasePage {

    private static final Logger LOG = LoggerFactory.getLogger(OpenmrsRegistrationPage.class);

    private static final String GIVEN_NAME = "input[name=\"givenName\"]";
    private static final String FAMILY_NAME = "input[name=\"familyName\"]";
    private static final String GENDER = "select[name=\"gender\"], #gender-field";
    private static final String BIRTH_DAY = "#birthdateDay-field";
    private static final String BIRTH_MONTH = "#birthdateMonth-field";
    private static final String BIRTH_YEAR = "#birthdateYear-field";
    private static final String ADDRESS_1 = "#address1";
    private static final String CITY = "#cityVillage";
    private static final String NEXT_BUTTON = "#next-button, #next, button:has-text('Next')";
    private static final String CONFIRM_BUTTON = "#submit, input[value='Confirm']";
    private static final String FORM_ROOT = "form, #registrationForm, .form-wrapper";

    public OpenmrsRegistrationPage(Page page) {
        super(page);
    }

    /** Waits until the registration form has rendered (name field, or form container fallback). */
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

    public OpenmrsRegistrationPage enterGivenName(String givenName) {
        type(GIVEN_NAME, givenName);
        return this;
    }

    public OpenmrsRegistrationPage enterFamilyName(String familyName) {
        type(FAMILY_NAME, familyName);
        return this;
    }

    public OpenmrsRegistrationPage enterName(String givenName, String familyName) {
        return enterGivenName(givenName).enterFamilyName(familyName);
    }

    /** Selects patient gender by OpenMRS code ({@code "M"} or {@code "F"}). */
    public OpenmrsRegistrationPage selectGender(String code) {
        page.locator(GENDER).first().selectOption(code);
        return this;
    }

    /** Enters an exact birth date; month is selected by its first real option to stay locale-safe. */
    public OpenmrsRegistrationPage enterBirthDate(int day, int year) {
        page.locator(BIRTH_DAY).first().fill(Integer.toString(day));
        page.locator(BIRTH_MONTH)
                .first()
                .selectOption(new com.microsoft.playwright.options.SelectOption().setIndex(1));
        page.locator(BIRTH_YEAR).first().fill(Integer.toString(year));
        return this;
    }

    public OpenmrsRegistrationPage enterAddressLine(String addressLine) {
        type(ADDRESS_1, addressLine);
        return this;
    }

    public OpenmrsRegistrationPage enterCity(String city) {
        type(CITY, city);
        return this;
    }

    /** Advances the wizard to the next step. */
    public OpenmrsRegistrationPage next() {
        click(NEXT_BUTTON);
        return this;
    }

    /** @return {@code true} when the final Confirm/submit control is visible. */
    public boolean isConfirmVisible() {
        return isVisible(CONFIRM_BUTTON);
    }

    /** Confirms / submits the registration. */
    public OpenmrsRegistrationPage confirm() {
        click(CONFIRM_BUTTON);
        return this;
    }
}
