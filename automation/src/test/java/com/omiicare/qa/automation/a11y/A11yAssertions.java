package com.omiicare.qa.automation.a11y;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;

/**
 * Fluent, opinionated assertions over a set of {@link A11yViolation}s produced by {@link AxeRunner}.
 *
 * <p>The default accessibility gate fails on any {@code critical} or {@code serious} impact, which
 * maps to WCAG blockers in most enterprise policies, while tolerating {@code moderate}/{@code minor}
 * findings that teams typically triage rather than block on. All filtering logic is exposed as pure
 * static helpers so it can be unit-tested without a browser.
 */
public final class A11yAssertions {

    private A11yAssertions() {
        // static utility
    }

    /**
     * Returns only the blocking ({@code critical} + {@code serious}) violations.
     *
     * @param violations the full violation set (may be empty); must not be {@code null}
     * @return a new list containing only blocking violations
     */
    public static List<A11yViolation> blocking(List<A11yViolation> violations) {
        Objects.requireNonNull(violations, "violations must not be null");
        return violations.stream().filter(A11yViolation::isBlocking).collect(Collectors.toList());
    }

    /**
     * Returns the violations whose impact matches the supplied level (case-insensitive).
     *
     * @param violations the full violation set; must not be {@code null}
     * @param impact the axe impact level to match (e.g. {@code "critical"})
     * @return a new list of matching violations
     */
    public static List<A11yViolation> withImpact(List<A11yViolation> violations, String impact) {
        Objects.requireNonNull(violations, "violations must not be null");
        Objects.requireNonNull(impact, "impact must not be null");
        return violations.stream()
                .filter(v -> impact.equalsIgnoreCase(v.impact()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the violations whose rule id is NOT in the supplied ignore list (case-insensitive). Use
     * this to suppress known, accepted-risk rules (e.g. a third-party widget's contrast issue).
     *
     * @param violations the full violation set; must not be {@code null}
     * @param ignoredRuleIds rule ids to drop; must not be {@code null}
     * @return a new filtered list
     */
    public static List<A11yViolation> excludingRules(
            List<A11yViolation> violations, List<String> ignoredRuleIds) {
        Objects.requireNonNull(violations, "violations must not be null");
        Objects.requireNonNull(ignoredRuleIds, "ignoredRuleIds must not be null");
        return violations.stream()
                .filter(v -> ignoredRuleIds.stream().noneMatch(id -> id.equalsIgnoreCase(v.id())))
                .collect(Collectors.toList());
    }

    /**
     * Asserts that there are no {@code critical} or {@code serious} accessibility violations. On
     * failure, produces a readable multi-line report of every blocking violation.
     *
     * @param violations the violations to evaluate; must not be {@code null}
     */
    public static void assertNoCriticalOrSerious(List<A11yViolation> violations) {
        List<A11yViolation> blocking = blocking(violations);
        Assertions.assertThat(blocking)
                .as("Blocking (critical/serious) accessibility violations:%n%s", render(blocking))
                .isEmpty();
    }

    /**
     * Asserts there are no violations at all (the strictest gate).
     *
     * @param violations the violations to evaluate; must not be {@code null}
     */
    public static void assertNoViolations(List<A11yViolation> violations) {
        Objects.requireNonNull(violations, "violations must not be null");
        Assertions.assertThat(violations)
                .as("Accessibility violations:%n%s", render(violations))
                .isEmpty();
    }

    /**
     * Renders a list of violations as an indented, human-readable block for failure messages.
     *
     * @param violations the violations to render; must not be {@code null}
     * @return a formatted string, or {@code "  <none>"} when empty
     */
    public static String render(List<A11yViolation> violations) {
        Objects.requireNonNull(violations, "violations must not be null");
        if (violations.isEmpty()) {
            return "  <none>";
        }
        return violations.stream().map(v -> "  - " + v).collect(Collectors.joining(System.lineSeparator()));
    }
}
