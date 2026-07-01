package com.omiicare.qa.automation.a11y;

import java.util.List;
import java.util.Objects;

/**
 * Immutable representation of a single accessibility violation as reported by axe-core.
 *
 * <p>axe-core groups failures by "rule" (its {@code id}), assigns an {@code impact} severity, links
 * to remediation guidance via {@code helpUrl}, and lists the specific DOM nodes that failed. This
 * record flattens the essential, assertion-relevant fields so the rest of the framework never has to
 * touch raw JSON.
 *
 * @param id the axe rule identifier (e.g. {@code "color-contrast"}, {@code "image-alt"})
 * @param impact severity as classified by axe ({@code "critical"}, {@code "serious"},
 *     {@code "moderate"}, {@code "minor"}); may be {@code null} if axe omitted it
 * @param description human-readable summary of the rule
 * @param help short remediation hint
 * @param helpUrl deep link to the axe rule documentation
 * @param nodeTargets CSS selectors of the offending DOM nodes (one entry per failing node)
 */
public record A11yViolation(
        String id,
        String impact,
        String description,
        String help,
        String helpUrl,
        List<String> nodeTargets) {

    /** Canonical axe impact level: a defect that must block release. */
    public static final String IMPACT_CRITICAL = "critical";

    /** Canonical axe impact level: a serious barrier for users with disabilities. */
    public static final String IMPACT_SERIOUS = "serious";

    /** Canonical axe impact level: a moderate barrier. */
    public static final String IMPACT_MODERATE = "moderate";

    /** Canonical axe impact level: a minor inconvenience. */
    public static final String IMPACT_MINOR = "minor";

    /**
     * Compact constructor enforcing non-null identity fields and defensively copying the node list so
     * the record stays genuinely immutable.
     */
    public A11yViolation {
        Objects.requireNonNull(id, "id must not be null");
        nodeTargets = nodeTargets == null ? List.of() : List.copyOf(nodeTargets);
    }

    /**
     * @return {@code true} when this violation's impact is {@code critical} or {@code serious}, the
     *     two levels that conventionally fail an accessibility gate
     */
    public boolean isBlocking() {
        return IMPACT_CRITICAL.equalsIgnoreCase(impact) || IMPACT_SERIOUS.equalsIgnoreCase(impact);
    }

    /**
     * @return the number of distinct DOM nodes that failed this rule
     */
    public int nodeCount() {
        return nodeTargets.size();
    }

    @Override
    public String toString() {
        return "[%s] %s (%d node%s) - %s | %s"
                .formatted(
                        impact == null ? "unknown" : impact,
                        id,
                        nodeCount(),
                        nodeCount() == 1 ? "" : "s",
                        description == null ? "" : description,
                        helpUrl == null ? "" : helpUrl);
    }
}
