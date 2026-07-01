package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the OpenMRS Reference Application login screen.
 *
 * <p>The login flow has two phases: (1) enter username/password and submit, then (2) select a
 * session login location from the list rendered as {@code <li id="...">} entries before the user is
 * fully authenticated into the home screen.
 *
 * <p>Stable selectors used: {@code #username}, {@code #password}, {@code #loginButton} and the
 * session-location list items {@code #loginLocations li} (each carries an {@code id}).
 */
public final class OpenmrsLoginPage extends PwBasePage {

    private static final Logger LOG = LoggerFactory.getLogger(OpenmrsLoginPage.class);

    private static final String USERNAME = "#username";
    private static final String PASSWORD = "#password";
    private static final String LOGIN_BUTTON = "#loginButton";
    private static final String LOCATION_LIST_ITEM = "#loginLocations li";
    private static final String ANY_LOCATION_ITEM = "li[id]";

    /** Relative path of the login page under the OpenMRS context root. */
    public static final String PATH = "/login.htm";

    /**
     * @param page the live Playwright page
     */
    public OpenmrsLoginPage(Page page) {
        super(page);
    }

    /**
     * Opens the login page by appending {@link #PATH} to the supplied base URL.
     *
     * @param baseUrl the OpenMRS context-root URL (no trailing slash required)
     * @return this page object for fluent chaining
     */
    public OpenmrsLoginPage open(String baseUrl) {
        navigate(trimTrailingSlash(baseUrl) + PATH);
        waitForVisible(USERNAME);
        return this;
    }

    /**
     * Enters credentials.
     *
     * @param username the username
     * @param password the password
     * @return this page object
     */
    public OpenmrsLoginPage enterCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        return this;
    }

    /**
     * Selects a session login location. The OpenMRS RefApp requires a location to be chosen before
     * the login button completes the flow. If a location whose id matches {@code preferredId} is
     * present it is chosen; otherwise the first available location is used. If no location list is
     * rendered (some configurations skip it) the method is a no-op.
     *
     * @param preferredId the preferred {@code <li>} id, may be {@code null} to accept any
     * @return this page object
     */
    public OpenmrsLoginPage chooseLoginLocation(String preferredId) {
        Locator items = page.locator(LOCATION_LIST_ITEM);
        if (items.count() == 0) {
            // Fall back to any id-bearing list item; if still none, nothing to select.
            items = page.locator(ANY_LOCATION_ITEM);
        }
        if (items.count() == 0) {
            LOG.debug("No session-location list rendered; skipping location selection");
            return this;
        }
        if (preferredId != null && !preferredId.isBlank()) {
            Locator preferred = page.locator("li#" + cssEscapeId(preferredId));
            if (preferred.count() > 0) {
                preferred.first().click();
                return this;
            }
        }
        items.first().click();
        return this;
    }

    /**
     * Submits the login form.
     *
     * @return this page object
     */
    public OpenmrsLoginPage submit() {
        click(LOGIN_BUTTON);
        return this;
    }

    /**
     * Convenience flow: open, enter credentials, choose a location and submit.
     *
     * @param baseUrl the OpenMRS context-root URL
     * @param username the username
     * @param password the password
     * @param preferredLocationId the preferred session-location id, may be {@code null}
     * @return a {@link OpenmrsHomePage} for the authenticated session
     */
    public OpenmrsHomePage loginAs(
            String baseUrl, String username, String password, String preferredLocationId) {
        open(baseUrl);
        enterCredentials(username, password);
        chooseLoginLocation(preferredLocationId);
        submit();
        OpenmrsHomePage home = new OpenmrsHomePage(page);
        home.waitUntilLoaded();
        return home;
    }

    /** @return {@code true} if the username field is currently visible. */
    public boolean isLoaded() {
        return isVisible(USERNAME);
    }

    /**
     * Waits for the login form to be present.
     *
     * @return this page object
     */
    public OpenmrsLoginPage waitUntilLoaded() {
        page.waitForSelector(
                USERNAME, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        return this;
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String cssEscapeId(String id) {
        // Minimal CSS identifier escaping for ids that may contain characters needing escaping.
        return id.replaceAll("([^a-zA-Z0-9_-])", "\\\\$1");
    }
}
