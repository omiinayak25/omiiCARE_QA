package com.omiicare.qa.automation.selenium;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central factory for Selenium {@link WebDriver} instances. Drivers are created from
 * {@link SeleniumConfig} so headless/headed mode, window size, and timeouts are all
 * configuration-driven. Chrome binaries and drivers are resolved automatically by Selenium
 * Manager (bundled with Selenium 4), so no explicit driver path is required.
 *
 * <p>This factory does not hold static state: each call to {@link #createDriver()} returns a
 * fresh, fully-configured driver that the caller owns and must {@link WebDriver#quit() quit}.
 */
public final class WebDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

    private final SeleniumConfig config;

    public WebDriverFactory(SeleniumConfig config) {
        this.config = config;
    }

    /** Convenience constructor using framework-resolved configuration. */
    public WebDriverFactory() {
        this(SeleniumConfig.fromFramework());
    }

    /**
     * Creates and configures a new {@link WebDriver}. Timeouts (implicit, page-load) are applied
     * from configuration. The window is sized for deterministic, responsive-layout-stable runs.
     *
     * @return a ready-to-use WebDriver positioned at a blank page
     * @throws IllegalArgumentException if the configured browser is unsupported
     */
    public WebDriver createDriver() {
        String browser = config.browser();
        WebDriver driver;
        switch (browser) {
            case "chrome":
            case "chrome-headless":
                driver = new ChromeDriver(buildChromeOptions());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported browser '" + browser + "'. Only 'chrome' is wired in this factory.");
        }
        applyTimeouts(driver);
        LOG.info(
                "Created {} WebDriver (headless={}, window={}x{})",
                browser,
                config.headless(),
                config.windowWidth(),
                config.windowHeight());
        return driver;
    }

    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments(
                "--window-size=" + config.windowWidth() + "," + config.windowHeight());
        // Accept the RefApp's self-signed / mixed certs gracefully in CI.
        options.setAcceptInsecureCerts(true);
        return options;
    }

    private void applyTimeouts(WebDriver driver) {
        WebDriver.Timeouts timeouts = driver.manage().timeouts();
        Duration implicit = config.implicitWait();
        if (!implicit.isZero()) {
            timeouts.implicitlyWait(implicit);
        }
        timeouts.pageLoadTimeout(config.pageLoadTimeout());
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(
                config.windowWidth(), config.windowHeight()));
    }
}
