package com.omiicare.qa.automation.selenium.pages;

import com.omiicare.qa.automation.selenium.SeleniumConfig;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page object for the OpenMRS Reference Application login screen.
 *
 * <p>Verified selectors: username {@code #username}, password {@code #password}, submit
 * {@code #loginButton}. The login form also requires a <em>session location</em> to be chosen
 * from a list of {@code <li id="...">} tiles before authentication succeeds.
 */
public final class LoginPage extends BasePage {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginButton");
    // Session-location tiles rendered as list items, each carrying a location uuid as its id.
    private static final By LOCATION_TILES = By.cssSelector("ul#sessionLocation li, #sessionLocation li");
    private static final By LOGIN_FORM = By.id("loginForm");

    public LoginPage(WebDriver driver, SeleniumConfig config) {
        super(driver, config);
    }

    /** Opens the login page at {@code /login.htm} and waits for the form to render. */
    public LoginPage open() {
        navigateTo("/login.htm");
        visible(USERNAME);
        return this;
    }

    /** True when the login form is on screen. */
    public boolean isLoaded() {
        return isDisplayed(USERNAME) && isDisplayed(LOGIN_BUTTON);
    }

    /**
     * Performs a full login: selects the configured session location (if the picker is present),
     * enters credentials, and submits.
     *
     * @return a {@link HomePage} representing the landing dashboard
     */
    public HomePage loginAs(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        selectConfiguredLocation();
        click(LOGIN_BUTTON);
        return new HomePage(driver, config);
    }

    /** Logs in using the credentials resolved from configuration. */
    public HomePage loginWithConfiguredCredentials() {
        return loginAs(config.username(), config.password());
    }

    /**
     * Selects a session-location tile whose visible text matches the configured fragment. Falls
     * back to the first available tile so the flow remains robust across RefApp data sets. No-op
     * when no location picker is present (some deployments skip it).
     */
    private void selectConfiguredLocation() {
        List<WebElement> tiles = driver.findElements(LOCATION_TILES);
        if (tiles.isEmpty()) {
            log.debug("No session-location picker present; skipping location selection");
            return;
        }
        String wanted = config.loginLocation().toLowerCase();
        for (WebElement tile : tiles) {
            String label = tile.getText() == null ? "" : tile.getText().trim().toLowerCase();
            if (!label.isEmpty() && label.contains(wanted)) {
                log.info("Selecting session location '{}'", tile.getText().trim());
                tile.click();
                return;
            }
        }
        log.info("Configured location '{}' not found; selecting first available tile", wanted);
        tiles.get(0).click();
    }

    /** Returns the inline error message text shown on failed login, or empty string if none. */
    public String errorMessage() {
        By error = By.cssSelector("#loginForm .error, .alert-danger, .field-error");
        return isDisplayed(error) ? text(error) : "";
    }
}
