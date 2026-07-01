package com.omiicare.qa.automation.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.selenium.pages.HomePage;
import com.omiicare.qa.automation.selenium.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * End-to-end UI smoke test for the OpenMRS Reference Application login flow, driven through the
 * Selenium page-object layer. Requires a live RefApp (default {@code o2.openmrs.org}) and a real
 * Chrome browser, so it is tagged {@code ui-e2e} and excluded from the default {@code mvn test}
 * build; run it explicitly with {@code -Dgroups=ui-e2e} (or the equivalent Surefire profile).
 *
 * <p>The test exercises the full happy path: open the login page, select a session location,
 * authenticate with configured credentials, and assert the home dashboard renders for an
 * authenticated session.
 */
@Tag("ui-e2e")
@DisplayName("OpenMRS RefApp — login UI smoke")
class OpenMrsLoginUiTest {

    private static final Logger LOG = LoggerFactory.getLogger(OpenMrsLoginUiTest.class);

    private SeleniumConfig config;
    private WebDriverFactory factory;
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        config = SeleniumConfig.fromFramework();
        factory = new WebDriverFactory(config);
        driver = factory.createDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("logs in with configured credentials and lands on the home dashboard")
    void logsInAndReachesHomeDashboard() {
        LoginPage loginPage = new LoginPage(driver, config).open();
        assertThat(loginPage.isLoaded())
                .as("login form should render at %s", config.baseUrl())
                .isTrue();

        HomePage homePage = loginPage.loginWithConfiguredCredentials();
        homePage.waitUntilLoaded();

        assertThat(homePage.isUserLoggedIn())
                .as("an authenticated session should be established after login")
                .isTrue();
        assertThat(homePage.isLoaded())
                .as("home dashboard tiles should be visible after login")
                .isTrue();

        LOG.info("Login UI smoke succeeded; landed at {}", homePage.currentUrl());
    }
}
