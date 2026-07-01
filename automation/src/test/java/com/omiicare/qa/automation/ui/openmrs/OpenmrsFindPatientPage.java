package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the OpenMRS Reference Application "Find Patient Record" search screen. The screen
 * exposes a single search input (id {@code #patient-search}) that filters a results table
 * ({@code #patient-search-results-table}) live as the operator types.
 */
public final class OpenmrsFindPatientPage extends PwBasePage {

    private static final Logger LOG = LoggerFactory.getLogger(OpenmrsFindPatientPage.class);

    private static final String SEARCH_INPUT =
            "#patient-search, input[placeholder*='Search'], input[type='text']";
    private static final String RESULTS_TABLE =
            "#patient-search-results-table, table.patient-search-results, table";
    private static final String RESULT_ROW =
            "#patient-search-results-table tbody tr, table tbody tr";

    /**
     * @param page the live Playwright page
     */
    public OpenmrsFindPatientPage(Page page) {
        super(page);
    }

    /**
     * Waits until the search input is visible.
     *
     * @return this page object
     */
    public OpenmrsFindPatientPage waitUntilLoaded() {
        page.waitForSelector(
                SEARCH_INPUT,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        LOG.debug("OpenMRS find-patient screen loaded at {}", currentUrl());
        return this;
    }

    /** @return {@code true} if the search input is visible. */
    public boolean isLoaded() {
        return isVisible(SEARCH_INPUT);
    }

    /**
     * Types a query into the patient search field. The RefApp searches incrementally, so no submit
     * is required.
     *
     * @param query the search term (name or identifier)
     * @return this page object
     */
    public OpenmrsFindPatientPage search(String query) {
        type(SEARCH_INPUT, query);
        return this;
    }

    /**
     * Waits for the results table to be present and returns the number of result rows currently
     * displayed.
     *
     * @return the count of result rows
     */
    public int resultCount() {
        page.waitForSelector(
                RESULTS_TABLE,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        Locator rows = page.locator(RESULT_ROW);
        return rows.count();
    }

    /** @return {@code true} if at least one patient result row is shown. */
    public boolean hasResults() {
        return resultCount() > 0;
    }
}
