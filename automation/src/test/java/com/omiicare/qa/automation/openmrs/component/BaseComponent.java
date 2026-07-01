package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Abstract base for reusable UI components (Page Component layer). A component wraps a Playwright
 * {@link Page} and, optionally, a root {@link Locator} that scopes all lookups. Components hold
 * locators + interactions for a reusable UI region only — NO assertions, NO business workflow.
 */
public abstract class BaseComponent {

    protected final Page page;
    protected final Locator root;

    protected BaseComponent(Page page) {
        this(page, null);
    }

    protected BaseComponent(Page page, Locator root) {
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }
        this.page = page;
        this.root = root;
    }

    /** Resolves a selector within this component's root (or the whole page if unscoped). */
    protected Locator within(String selector) {
        return root == null ? page.locator(selector) : root.locator(selector);
    }

    /** @return the underlying Playwright page. */
    public Page page() {
        return page;
    }
}
