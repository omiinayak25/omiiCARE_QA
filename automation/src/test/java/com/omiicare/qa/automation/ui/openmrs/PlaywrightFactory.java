package com.omiicare.qa.automation.ui.openmrs;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central factory that owns the lifecycle of Playwright, the {@link Browser}, a {@link
 * BrowserContext} and a {@link Page}. All runtime knobs (browser channel, headed/headless mode,
 * slow-motion delay, viewport, base URL and timeouts) are resolved exclusively through {@link
 * FrameworkConfig}; nothing is hardcoded in the test bodies.
 *
 * <p>Typical usage from a test:
 *
 * <pre>{@code
 * PlaywrightFactory factory = new PlaywrightFactory();
 * Page page = factory.createPage();   // launches browser + context + page
 * try {
 *     // drive page objects...
 * } finally {
 *     factory.close();                 // tears everything down
 * }
 * }</pre>
 *
 * <p>The factory uses Playwright's bundled browser binaries; for {@code chrome} it relies on the
 * locally installed Google Chrome via the {@code channel} option. Instances are intended to be used
 * by a single thread (one factory per test); they are not thread-safe.
 */
public final class PlaywrightFactory implements AutoCloseable {

    /** Config key for the browser channel (e.g. {@code chrome}, {@code msedge}). */
    public static final String KEY_CHANNEL = "omii.pw.channel";
    /** Config key for headless mode ({@code true}/{@code false}). */
    public static final String KEY_HEADLESS = "omii.pw.headless";
    /** Config key for slow-motion delay in milliseconds (applied only when headed). */
    public static final String KEY_SLOWMO = "omii.pw.slowmo";
    /** Config key for the default navigation/action timeout in milliseconds. */
    public static final String KEY_TIMEOUT = "omii.pw.timeout";
    /** Config key for the viewport width. */
    public static final String KEY_VIEWPORT_WIDTH = "omii.pw.viewport.width";
    /** Config key for the viewport height. */
    public static final String KEY_VIEWPORT_HEIGHT = "omii.pw.viewport.height";
    /** Config key for the OpenMRS base URL the page objects navigate against. */
    public static final String KEY_BASE_URL = "omii.openmrs.ui.baseUrl";

    private static final Logger LOG = LoggerFactory.getLogger(PlaywrightFactory.class);

    private final FrameworkConfig config;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    /** Builds a factory backed by the shared {@link FrameworkConfig} singleton. */
    public PlaywrightFactory() {
        this(FrameworkConfig.get());
    }

    /**
     * Builds a factory backed by an explicit configuration source (useful for unit-testing the
     * resolution of options without launching a browser).
     *
     * @param config the configuration source, never {@code null}
     */
    public PlaywrightFactory(FrameworkConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }
        this.config = config;
    }

    /** @return the configured browser channel, defaulting to {@code chrome}. */
    public String channel() {
        return config.get(KEY_CHANNEL, "chrome");
    }

    /** @return whether the browser should run headless, defaulting to {@code true}. */
    public boolean headless() {
        return Boolean.parseBoolean(config.get(KEY_HEADLESS, "true"));
    }

    /**
     * @return the slow-motion delay in milliseconds. Only meaningful when running headed; defaults
     *     to {@code 0}.
     */
    public double slowMo() {
        return parseDouble(config.get(KEY_SLOWMO, "0"), 0d);
    }

    /** @return the default timeout in milliseconds applied to navigation and actions. */
    public double timeoutMillis() {
        return parseDouble(config.get(KEY_TIMEOUT, "30000"), 30_000d);
    }

    /** @return the configured base URL for the OpenMRS web app. */
    public String baseUrl() {
        return config.get(KEY_BASE_URL, "https://o2.openmrs.org/openmrs");
    }

    /**
     * Launches Playwright, the browser, a fresh context and a page using the resolved configuration.
     * Subsequent calls return the same {@link Page} until {@link #close()} is invoked.
     *
     * @return a ready-to-use {@link Page}
     */
    public Page createPage() {
        if (page != null) {
            return page;
        }
        boolean headless = headless();
        double slowMo = slowMo();
        String channel = channel();
        LOG.info(
                "Launching Playwright browser channel={} headless={} slowMo={}ms",
                channel,
                headless,
                slowMo);

        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions =
                new BrowserType.LaunchOptions().setHeadless(headless).setChannel(channel);
        // slowMo only has a visible effect when headed; passing it headless is harmless.
        if (slowMo > 0) {
            launchOptions.setSlowMo(slowMo);
        }
        browser = playwright.chromium().launch(launchOptions);

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        int width = (int) parseDouble(config.get(KEY_VIEWPORT_WIDTH, "1440"), 1440d);
        int height = (int) parseDouble(config.get(KEY_VIEWPORT_HEIGHT, "900"), 900d);
        contextOptions.setViewportSize(width, height);
        context = browser.newContext(contextOptions);

        double timeout = timeoutMillis();
        context.setDefaultTimeout(timeout);
        context.setDefaultNavigationTimeout(timeout);

        page = context.newPage();
        return page;
    }

    /**
     * Captures a full-page screenshot of the current page into the target/screenshots directory.
     *
     * @param name a file-name stem (without extension)
     * @return the path the screenshot was written to, or {@code null} if no page is open
     */
    public Path screenshot(String name) {
        if (page == null) {
            return null;
        }
        Path path = Paths.get("target", "screenshots", name + ".png");
        page.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));
        return path;
    }

    /** @return the live {@link Page}, or {@code null} if {@link #createPage()} was not yet called. */
    public Page page() {
        return page;
    }

    /** Closes the page, context, browser and Playwright instance in reverse order. Idempotent. */
    @Override
    public void close() {
        try {
            if (context != null) {
                context.close();
            }
        } finally {
            try {
                if (browser != null) {
                    browser.close();
                }
            } finally {
                if (playwright != null) {
                    playwright.close();
                }
                page = null;
                context = null;
                browser = null;
                playwright = null;
            }
        }
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return fallback;
        }
    }
}
