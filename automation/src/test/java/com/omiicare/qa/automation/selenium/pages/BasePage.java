package com.omiicare.qa.automation.selenium.pages;

import com.omiicare.qa.automation.selenium.SeleniumConfig;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Foundation for all page objects. Centralizes the {@link WebDriver}, an explicit
 * {@link WebDriverWait}, and a small library of resilient interaction helpers (waited clicks,
 * typed input, visibility/presence checks) so individual page objects stay declarative and free
 * of timing boilerplate.
 *
 * <p>All waits are explicit and bounded by {@code selenium.timeout.explicit.seconds} — page
 * objects should never call {@link Thread#sleep(long)}.
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final WebDriver driver;
    protected final SeleniumConfig config;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver, SeleniumConfig config) {
        this.driver = driver;
        this.config = config;
        this.wait = new WebDriverWait(driver, config.explicitWait());
    }

    /** Navigates to an absolute or base-relative path and waits for the document to be ready. */
    protected void navigateTo(String path) {
        String url =
                path.startsWith("http")
                        ? path
                        : config.baseUrl() + (path.startsWith("/") ? path : "/" + path);
        log.info("Navigating to {}", url);
        driver.get(url);
        waitForDocumentReady();
    }

    /** Blocks until {@code document.readyState === 'complete'} or the explicit timeout elapses. */
    protected void waitForDocumentReady() {
        wait.until(
                d ->
                        "complete"
                                .equals(
                                        ((JavascriptExecutor) d)
                                                .executeScript("return document.readyState")));
    }

    /** Waits for an element to be visible and returns it. */
    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits for an element to be clickable and clicks it. */
    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Waits for an element to be visible, clears it, and types the supplied text. */
    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Returns the trimmed visible text of an element once it is present. */
    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    /** True if at least one matching element is currently present and displayed. */
    protected boolean isDisplayed(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /** Waits until at least one element matching the locator is present in the DOM. */
    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /** Waits until the URL contains the given fragment (useful for post-action redirects). */
    protected boolean waitForUrlContains(String fragment) {
        return Boolean.TRUE.equals(
                wait.until(ExpectedConditions.urlContains(fragment)));
    }

    /** Short, bounded poll for an optional element; never throws on absence. */
    protected boolean awaitOptional(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** Current page URL. */
    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
