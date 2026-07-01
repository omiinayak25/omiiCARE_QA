package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Page;

/**
 * Reusable search-input component (Page Component layer). Wraps a single search box; the concrete
 * selector is injected so it works for patient search, provider search, etc.
 */
public final class SearchComponent extends BaseComponent {

    private final String inputSelector;

    public SearchComponent(Page page, String inputSelector) {
        super(page);
        this.inputSelector = inputSelector;
    }

    /** Factory for the OpenMRS "Find Patient Record" search box. */
    public static SearchComponent findPatient(Page page) {
        return new SearchComponent(page, "#patient-search, input[placeholder*=\"earch\" i]");
    }

    public boolean isVisible() {
        return within(inputSelector).first().isVisible();
    }

    public SearchComponent type(String query) {
        within(inputSelector).first().fill(query);
        return this;
    }

    public String value() {
        return within(inputSelector).first().inputValue();
    }
}
