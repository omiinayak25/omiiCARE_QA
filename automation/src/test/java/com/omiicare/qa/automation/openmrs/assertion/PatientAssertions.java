package com.omiicare.qa.automation.openmrs.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.openmrs.component.DataTableComponent;

/**
 * Patient UI assertions (Assertion layer).
 */
public final class PatientAssertions {

    private final Page page;

    private PatientAssertions(Page page) {
        this.page = page;
    }

    public static PatientAssertions on(Page page) {
        return new PatientAssertions(page);
    }

    /** Asserts registration completed: the wizard closed and the new patient's name is shown. */
    public PatientAssertions wasRegistered(String familyName) {
        assertThat(page.url()).as("left registration wizard").doesNotContain("registerPatient.page");
        assertThat(page.locator("body").textContent())
                .as("patient dashboard shows '%s'", familyName)
                .contains(familyName);
        return this;
    }

    /** Asserts a results table contains at least one row for the given text. */
    public PatientAssertions tableContains(DataTableComponent table, String text) {
        assertThat(table.hasRowContaining(text))
                .as("results table contains a row for '%s'", text)
                .isTrue();
        return this;
    }
}
