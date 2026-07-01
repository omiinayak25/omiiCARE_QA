package com.omiicare.qa.automation.a11y;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import io.restassured.path.json.JsonPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injects the axe-core engine into a live Playwright {@link Page} and executes an accessibility
 * audit, returning a list of {@link A11yViolation} records.
 *
 * <p>Workflow:
 *
 * <ol>
 *   <li>Inject the axe-core bundle from a configurable CDN URL ({@code a11y.axe.cdn}, defaulting to
 *       jsDelivr) via {@link Page#addScriptTag}.
 *   <li>Invoke {@code axe.run(...)} in the page context through {@link Page#evaluate}, which resolves
 *       the returned Promise and hands back the results object as a Java {@link Map}.
 *   <li>Parse the {@code violations} array into immutable records.
 * </ol>
 *
 * <p>This class performs no assertions; it is a pure producer of findings. Combine it with {@link
 * A11yAssertions} to enforce a gate. Network/browser access happens only when {@link #analyze(Page)}
 * is called, so constructing a runner is side-effect free.
 */
public final class AxeRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AxeRunner.class);

    /** Default CDN for the axe-core UMD bundle when no override is configured. */
    public static final String DEFAULT_AXE_CDN =
            "https://cdn.jsdelivr.net/npm/axe-core@4.10.2/axe.min.js";

    /**
     * JavaScript that runs axe against the whole document and returns the full results object. axe.run
     * returns a Promise; Playwright's {@link Page#evaluate} awaits it automatically.
     */
    private static final String RUN_AXE_JS =
            "() => axe.run(document, {resultTypes: ['violations']}).then(r => r)";

    private final String axeCdnUrl;

    /** Creates a runner that resolves the axe CDN URL from {@link FrameworkConfig}. */
    public AxeRunner() {
        this(FrameworkConfig.get().get("a11y.axe.cdn", DEFAULT_AXE_CDN));
    }

    /**
     * Creates a runner with an explicit axe CDN URL (useful for offline mirrors or pinning a version).
     *
     * @param axeCdnUrl absolute URL to the axe-core UMD/min bundle
     */
    public AxeRunner(String axeCdnUrl) {
        this.axeCdnUrl = Objects.requireNonNull(axeCdnUrl, "axeCdnUrl must not be null");
    }

    /**
     * Runs an accessibility audit against the current state of the supplied page.
     *
     * @param page a Playwright page already navigated to the target URL
     * @return the violations axe-core found, possibly empty, never {@code null}
     */
    @SuppressWarnings("unchecked")
    public List<A11yViolation> analyze(Page page) {
        Objects.requireNonNull(page, "page must not be null");
        LOG.info("Injecting axe-core from {}", axeCdnUrl);
        page.addScriptTag(new Page.AddScriptTagOptions().setUrl(axeCdnUrl));

        Object raw = page.evaluate(RUN_AXE_JS);
        if (!(raw instanceof Map)) {
            LOG.warn("axe.run returned unexpected type {}; treating as zero violations",
                    raw == null ? "null" : raw.getClass().getName());
            return List.of();
        }
        Map<String, Object> results = (Map<String, Object>) raw;
        Object violationsObj = results.get("violations");
        if (!(violationsObj instanceof List)) {
            return List.of();
        }

        List<Map<String, Object>> rawViolations = (List<Map<String, Object>>) violationsObj;
        List<A11yViolation> parsed = new ArrayList<>(rawViolations.size());
        for (Map<String, Object> v : rawViolations) {
            parsed.add(toViolation(v));
        }
        LOG.info("axe-core reported {} violation rule(s)", parsed.size());
        return List.copyOf(parsed);
    }

    /**
     * Parses a single axe results JSON string (e.g. captured from a fixture or another transport)
     * into violation records. Useful for unit-testing parsing without a browser.
     *
     * @param json the JSON document containing a top-level {@code violations} array
     * @return parsed violations, never {@code null}
     */
    public static List<A11yViolation> parse(String json) {
        Objects.requireNonNull(json, "json must not be null");
        JsonPath jp = new JsonPath(json);
        List<Map<String, Object>> rawViolations = jp.getList("violations");
        if (rawViolations == null) {
            return List.of();
        }
        List<A11yViolation> parsed = new ArrayList<>(rawViolations.size());
        for (Map<String, Object> v : rawViolations) {
            parsed.add(toViolation(v));
        }
        return List.copyOf(parsed);
    }

    @SuppressWarnings("unchecked")
    private static A11yViolation toViolation(Map<String, Object> v) {
        String id = asString(v.get("id"));
        String impact = asString(v.get("impact"));
        String description = asString(v.get("description"));
        String help = asString(v.get("help"));
        String helpUrl = asString(v.get("helpUrl"));

        List<String> targets = new ArrayList<>();
        Object nodesObj = v.get("nodes");
        if (nodesObj instanceof List<?> nodes) {
            for (Object nodeObj : nodes) {
                if (nodeObj instanceof Map<?, ?> node) {
                    Object targetObj = node.get("target");
                    if (targetObj instanceof List<?> targetList) {
                        for (Object t : targetList) {
                            targets.add(String.valueOf(t));
                        }
                    } else if (targetObj != null) {
                        targets.add(String.valueOf(targetObj));
                    }
                }
            }
        }
        return new A11yViolation(id, impact, description, help, helpUrl, targets);
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
