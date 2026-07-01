package com.omiicare.qa.automation.a11y;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * End-to-end accessibility audit driven by Playwright + axe-core against the OpenMRS Reference
 * Application login page.
 *
 * <p>This test launches a real browser and reaches a live SUT, so it is tagged {@code ui-e2e} and is
 * excluded from the default {@code mvn test} run. Configure the target via {@code a11y.target.url}
 * (defaults to the OpenMRS RefApp login).
 */
@Tag("ui-e2e")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AxeAccessibilityE2eTest {

    private static final Logger LOG = LoggerFactory.getLogger(AxeAccessibilityE2eTest.class);

    /** Default target page: the OpenMRS RefApp login screen. */
    private static final String DEFAULT_TARGET_URL = "https://o2.openmrs.org/openmrs/login.htm";

    private Playwright playwright;
    private Browser browser;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        boolean headless =
                Boolean.parseBoolean(FrameworkConfig.get().get("a11y.headless", "true"));
        browser =
                playwright
                        .chromium()
                        .launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @AfterAll
    void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    @DisplayName("OpenMRS login page has no critical or serious accessibility violations")
    void loginPageHasNoBlockingViolations() {
        String url = FrameworkConfig.get().get("a11y.target.url", DEFAULT_TARGET_URL);
        Page page = browser.newPage();
        try {
            LOG.info("Navigating to {} for accessibility audit", url);
            page.navigate(url);
            page.waitForLoadState();

            List<A11yViolation> violations = new AxeRunner().analyze(page);
            LOG.info("Accessibility report:%n{}", A11yAssertions.render(violations));

            A11yAssertions.assertNoCriticalOrSerious(violations);
        } finally {
            page.close();
        }
    }
}
