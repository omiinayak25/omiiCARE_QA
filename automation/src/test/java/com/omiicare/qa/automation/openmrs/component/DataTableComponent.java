package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Generic HTML data-table component (Page Component layer). Reusable across any results/list table
 * (patients, appointments, encounters). Row/cell reads only — no assertions.
 */
public final class DataTableComponent extends BaseComponent {

    public DataTableComponent(Page page, String tableSelector) {
        super(page, page.locator(tableSelector).first());
    }

    /** Number of body rows. */
    public int rowCount() {
        return within("tbody tr").count();
    }

    /** True if any body row contains the given text. */
    public boolean hasRowContaining(String text) {
        return within("tbody tr").filter(new Locator.FilterOptions().setHasText(text)).count() > 0;
    }

    /** The first body row containing the given text (for further interaction). */
    public Locator rowContaining(String text) {
        return within("tbody tr").filter(new Locator.FilterOptions().setHasText(text)).first();
    }

    /** Whole-table text (useful for coarse content assertions in the assertion layer). */
    public String text() {
        String t = root.textContent();
        return t == null ? "" : t;
    }
}
