package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base for all Playwright page objects. Encapsulates the {@link Page} handle plus a small,
 * intention-revealing toolbox (navigation, typing, clicking, waiting, text extraction) so the
 * concrete page objects stay declarative and free of raw Playwright boilerplate.
 *
 * <p>Selectors are passed as Playwright selector strings (CSS, text, role, etc.). All waits honor
 * the context-level default timeout configured by {@link PlaywrightFactory}.
 */
public abstract class PwBasePage {

    private static final Logger LOG = LoggerFactory.getLogger(PwBasePage.class);

    /** The Playwright page driving this object. */
    protected final Page page;

    /**
     * @param page the live Playwright page, never {@code null}
     */
    protected PwBasePage(Page page) {
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }
        this.page = page;
    }

    /** @return the underlying Playwright {@link Page}. */
    public Page page() {
        return page;
    }

    /** @return the current page URL. */
    public String currentUrl() {
        return page.url();
    }

    /** @return the current page title. */
    public String title() {
        return page.title();
    }

    /**
     * Navigates to an absolute URL and waits for the network to settle.
     *
     * @param url an absolute URL
     */
    protected void navigate(String url) {
        LOG.debug("Navigating to {}", url);
        page.navigate(url);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    /**
     * Resolves a locator for a selector.
     *
     * @param selector a Playwright selector
     * @return the {@link Locator}
     */
    protected Locator locator(String selector) {
        return page.locator(selector);
    }

    /**
     * Waits until an element matching the selector is attached and visible.
     *
     * @param selector a Playwright selector
     * @return the located, now-visible {@link Locator}
     */
    protected Locator waitForVisible(String selector) {
        page.waitForSelector(
                selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        return page.locator(selector);
    }

    /**
     * Types text into the first element matching the selector, after waiting for it to be visible.
     *
     * @param selector a Playwright selector
     * @param text the text to enter
     */
    protected void type(String selector, String text) {
        waitForVisible(selector).fill(text);
    }

    /**
     * Clicks the first element matching the selector, after waiting for it to be visible.
     *
     * @param selector a Playwright selector
     */
    protected void click(String selector) {
        waitForVisible(selector).click();
    }

    /**
     * @param selector a Playwright selector
     * @return {@code true} if at least one matching element is visible
     */
    protected boolean isVisible(String selector) {
        Locator locator = page.locator(selector);
        return locator.count() > 0 && locator.first().isVisible();
    }

    /**
     * Reads the trimmed text content of the first element matching the selector.
     *
     * @param selector a Playwright selector
     * @return the trimmed text, or an empty string if absent
     */
    protected String textOf(String selector) {
        Locator locator = page.locator(selector);
        if (locator.count() == 0) {
            return "";
        }
        String text = locator.first().textContent();
        return text == null ? "" : text.trim();
    }
}
