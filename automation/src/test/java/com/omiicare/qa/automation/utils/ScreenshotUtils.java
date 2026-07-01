package com.omiicare.qa.automation.utils;

import com.microsoft.playwright.Page;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Screenshot helper (Utility layer). Thin, reusable wrapper over Playwright's screenshot API — no
 * business logic. Files land under {@code target/screenshots} by default.
 */
public final class ScreenshotUtils {

    private static final Path DEFAULT_DIR = Paths.get("target", "screenshots");

    private ScreenshotUtils() {}

    /** Captures a full-page PNG named {@code <name>-<uniqueSuffix>.png} under the default dir. */
    public static Path capture(Page page, String name) {
        return capture(page, DEFAULT_DIR, name);
    }

    /** Captures a full-page PNG into {@code dir}. */
    public static Path capture(Page page, Path dir, String name) {
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }
        FileUtils.ensureDir(dir);
        String safe = (name == null ? "screenshot" : name).replaceAll("[^A-Za-z0-9._-]", "_");
        Path out = dir.resolve(safe + "-" + RandomDataUtils.uniqueSuffix() + ".png");
        page.screenshot(new Page.ScreenshotOptions().setPath(out).setFullPage(true));
        return out;
    }
}
