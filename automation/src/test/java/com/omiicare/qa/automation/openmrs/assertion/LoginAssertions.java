package com.omiicare.qa.automation.openmrs.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.openmrs.component.NavigationComponent;
import com.omiicare.qa.automation.openmrs.component.ToastComponent;

/**
 * Login/authentication UI assertions (Assertion layer).
 */
public final class LoginAssertions {

    private final Page page;

    private LoginAssertions(Page page) {
        this.page = page;
    }

    public static LoginAssertions on(Page page) {
        return new LoginAssertions(page);
    }

    /** Asserts the session reached the OpenMRS home dashboard. */
    public LoginAssertions isOnHomeDashboard() {
        assertThat(page.url()).as("home URL").contains("home.page");
        assertThat(new NavigationComponent(page).isHomeLoaded())
                .as("home dashboard tiles visible")
                .isTrue();
        return this;
    }

    /** Asserts a bad login was rejected (error shown, no dashboard access). */
    public LoginAssertions wasRejected() {
        assertThat(page.url()).as("did not reach home").doesNotContain("home.page");
        assertThat(new ToastComponent(page).hasMessageContaining("Invalid username"))
                .as("invalid-credentials error shown")
                .isTrue();
        return this;
    }
}
