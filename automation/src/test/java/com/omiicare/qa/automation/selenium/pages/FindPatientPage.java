package com.omiicare.qa.automation.selenium.pages;

import com.omiicare.qa.automation.selenium.SeleniumConfig;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for the OpenMRS Reference Application "Find Patient Record" search workflow
 * ({@code coreapps.findPatient}). Models the search box and the results table so end-to-end
 * tests can search for a previously registered patient and assert on the returned rows.
 *
 * <p>Verified entry anchor on the home dashboard: {@code a[href*='app=coreapps.findPatient']}.
 */
public final class FindPatientPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("patient-search");
    private static final By RESULTS_TABLE = By.id("patient-search-results-table");
    private static final By RESULT_ROWS = By.cssSelector("#patient-search-results-table tbody tr");
    private static final By NO_RESULTS = By.cssSelector(".no-results, #no-results");

    public FindPatientPage(WebDriver driver, SeleniumConfig config) {
        super(driver, config);
    }

    /** True once the search interface has rendered. */
    public boolean isLoaded() {
        return isDisplayed(SEARCH_INPUT);
    }

    /** Types a query into the patient search box; the RefApp searches as you type. */
    public FindPatientPage search(String query) {
        type(SEARCH_INPUT, query);
        return this;
    }

    /**
     * Waits (bounded) for the results table to render at least one row.
     *
     * @return true if one or more result rows appeared, false on timeout
     */
    public boolean hasResults() {
        return awaitOptional(RESULTS_TABLE, config.explicitWait())
                && !driver.findElements(RESULT_ROWS).isEmpty();
    }

    /** Number of patient rows currently shown in the results table. */
    public int resultCount() {
        return driver.findElements(RESULT_ROWS).size();
    }

    /** Waits a short, bounded interval for at least one result row to appear. */
    public FindPatientPage waitForAnyResult() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(RESULT_ROWS, 0));
        return this;
    }
}
