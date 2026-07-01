package com.omiicare.qa.automation.selenium.pages;

import com.omiicare.qa.automation.selenium.SeleniumConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the OpenMRS Reference Application patient-registration workflow
 * ({@code registrationapp}). The RefApp registration is a multi-step wizard (Demographics →
 * Contact Info → Confirm); this object models the first demographics step and submission, which
 * is sufficient for happy-path registration smoke coverage.
 *
 * <p>Field selectors below target the RefApp's stable field {@code name} attributes. They are
 * exposed as helpers rather than asserted here so end-to-end tests can drive them.
 */
public final class RegistrationPage extends BasePage {

    private static final By GIVEN_NAME = By.name("givenName");
    private static final By FAMILY_NAME = By.name("familyName");
    private static final By GENDER_SELECT = By.id("gender-field");
    private static final By BIRTH_DAY = By.id("birthdateDay-field");
    private static final By BIRTH_MONTH = By.id("birthdateMonth-field");
    private static final By BIRTH_YEAR = By.id("birthdateYear-field");
    private static final By NEXT_BUTTON = By.cssSelector("#next-button, .submitButton, input[value=\"Next\"]");
    private static final By CONFIRM_BUTTON = By.id("submit");
    private static final By REGISTRATION_FORM = By.id("registration-form");

    public RegistrationPage(WebDriver driver, SeleniumConfig config) {
        super(driver, config);
    }

    /** True once the demographics step has rendered. */
    public boolean isLoaded() {
        return isDisplayed(GIVEN_NAME) || isDisplayed(REGISTRATION_FORM);
    }

    /** Enters the patient's given (first) name. */
    public RegistrationPage enterGivenName(String givenName) {
        type(GIVEN_NAME, givenName);
        return this;
    }

    /** Enters the patient's family (last) name. */
    public RegistrationPage enterFamilyName(String familyName) {
        type(FAMILY_NAME, familyName);
        return this;
    }

    /** Enters the patient's date of birth across the day/month/year fields. */
    public RegistrationPage enterBirthDate(String day, String month, String year) {
        type(BIRTH_DAY, day);
        type(BIRTH_MONTH, month);
        type(BIRTH_YEAR, year);
        return this;
    }

    /** Advances the wizard to the next step. */
    public RegistrationPage clickNext() {
        click(NEXT_BUTTON);
        return this;
    }

    /** Confirms and submits the registration on the final wizard step. */
    public void confirmRegistration() {
        click(CONFIRM_BUTTON);
    }
}
