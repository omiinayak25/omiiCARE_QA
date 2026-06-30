package com.omiicare.qa.automation.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UI end-to-end suite (Playwright) against the React SUT. Tagged {@code ui-e2e}: excluded from the
 * default build (it needs a running frontend and a downloaded browser) and run with {@code -Pe2e}.
 * Drives the app exclusively via the stable {@code data-testid} selectors the frontend exposes.
 */
@Tag("ui-e2e")
class LoginUiE2ETest {

    private static Playwright playwright;
    private static Browser browser;
    private static String baseUrl;

    @BeforeAll
    static void launch() {
        FrameworkConfig config = FrameworkConfig.get();
        baseUrl = config.get("omii.frontend.baseUrl", "http://localhost:5173");
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(config.get("omii.ui.headless", "true"));
        browser =
                playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @AfterAll
    static void shutdown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void signsInWithDemoCredentials() {
        Page page = browser.newPage();
        page.navigate(baseUrl + "/login");
        FrameworkConfig config = FrameworkConfig.get();
        page.getByTestId("login-username").fill(config.get("omii.sut.username", "demo.admin"));
        page.getByTestId("login-password").fill(config.get("omii.sut.password", "Admin@12345"));
        page.getByTestId("login-submit").click();
        page.getByTestId("dashboard").waitFor();
        assertThat(page.getByRole(AriaRole.HEADING).first().textContent()).contains("Welcome");
        page.close();
    }
}
