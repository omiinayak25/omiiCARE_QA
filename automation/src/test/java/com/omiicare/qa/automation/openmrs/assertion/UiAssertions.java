package com.omiicare.qa.automation.openmrs.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Page;

/**
 * Generic, fluent UI assertions over a Playwright {@link Page} (Assertion layer). Centralises UI
 * verification so page objects and components remain assertion-free.
 */
public final class UiAssertions {

    private final Page page;

    private UiAssertions(Page page) {
        this.page = page;
    }

    public static UiAssertions on(Page page) {
        return new UiAssertions(page);
    }

    public UiAssertions urlContains(String fragment) {
        assertThat(page.url()).as("current URL contains '%s'", fragment).contains(fragment);
        return this;
    }

    public UiAssertions urlDoesNotContain(String fragment) {
        assertThat(page.url()).as("current URL excludes '%s'", fragment).doesNotContain(fragment);
        return this;
    }

    public UiAssertions elementVisible(String selector) {
        assertThat(page.locator(selector).first().isVisible())
                .as("element visible: %s", selector)
                .isTrue();
        return this;
    }

    public UiAssertions bodyContains(String text) {
        String body = page.locator("body").textContent();
        assertThat(body).as("page body contains '%s'", text).contains(text);
        return this;
    }
}
