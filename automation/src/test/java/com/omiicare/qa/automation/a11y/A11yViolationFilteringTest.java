package com.omiicare.qa.automation.a11y;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for accessibility violation filtering and parsing. These run with no SUT, no
 * browser, and no network — therefore they are intentionally <em>untagged</em> and execute as part of
 * the default build.
 */
class A11yViolationFilteringTest {

    private static A11yViolation violation(String id, String impact) {
        return new A11yViolation(
                id, impact, id + " description", "fix it", "https://help/" + id, List.of("#" + id));
    }

    @Test
    @DisplayName("isBlocking() is true only for critical and serious impacts")
    void isBlockingCoversCriticalAndSerious() {
        assertThat(violation("a", "critical").isBlocking()).isTrue();
        assertThat(violation("b", "SERIOUS").isBlocking()).isTrue();
        assertThat(violation("c", "moderate").isBlocking()).isFalse();
        assertThat(violation("d", "minor").isBlocking()).isFalse();
        assertThat(violation("e", null).isBlocking()).isFalse();
    }

    @Test
    @DisplayName("blocking() retains only critical/serious violations")
    void blockingFiltersToBlockingOnly() {
        List<A11yViolation> all =
                List.of(
                        violation("color-contrast", "serious"),
                        violation("image-alt", "critical"),
                        violation("region", "moderate"),
                        violation("landmark", "minor"));

        List<A11yViolation> blocking = A11yAssertions.blocking(all);

        assertThat(blocking).extracting(A11yViolation::id).containsExactly("color-contrast", "image-alt");
    }

    @Test
    @DisplayName("withImpact() matches case-insensitively")
    void withImpactIsCaseInsensitive() {
        List<A11yViolation> all =
                List.of(violation("a", "Critical"), violation("b", "critical"), violation("c", "minor"));

        assertThat(A11yAssertions.withImpact(all, "CRITICAL")).hasSize(2);
        assertThat(A11yAssertions.withImpact(all, "minor")).hasSize(1);
    }

    @Test
    @DisplayName("excludingRules() drops ignored rule ids case-insensitively")
    void excludingRulesSuppressesKnownRules() {
        List<A11yViolation> all =
                List.of(
                        violation("color-contrast", "serious"),
                        violation("image-alt", "critical"),
                        violation("region", "moderate"));

        List<A11yViolation> filtered =
                A11yAssertions.excludingRules(all, List.of("COLOR-CONTRAST", "region"));

        assertThat(filtered).extracting(A11yViolation::id).containsExactly("image-alt");
    }

    @Test
    @DisplayName("assertNoCriticalOrSerious passes when only moderate/minor remain")
    void assertNoCriticalOrSeriousPassesForNonBlocking() {
        List<A11yViolation> all =
                List.of(violation("region", "moderate"), violation("landmark", "minor"));

        A11yAssertions.assertNoCriticalOrSerious(all);
    }

    @Test
    @DisplayName("assertNoCriticalOrSerious fails and reports the blocking rule")
    void assertNoCriticalOrSeriousFailsForBlocking() {
        List<A11yViolation> all = List.of(violation("image-alt", "critical"));

        assertThatThrownBy(() -> A11yAssertions.assertNoCriticalOrSerious(all))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("image-alt");
    }

    @Test
    @DisplayName("AxeRunner.parse extracts id, impact, and node targets from axe JSON")
    void parseExtractsViolations() {
        String json =
                """
                {
                  "violations": [
                    {
                      "id": "color-contrast",
                      "impact": "serious",
                      "description": "Elements must have sufficient color contrast",
                      "help": "Ensure contrast",
                      "helpUrl": "https://dequeuniversity.com/rules/axe/4.10/color-contrast",
                      "nodes": [
                        { "target": ["#login .field"] },
                        { "target": ["button.submit"] }
                      ]
                    }
                  ]
                }
                """;

        List<A11yViolation> parsed = AxeRunner.parse(json);

        assertThat(parsed).hasSize(1);
        A11yViolation v = parsed.get(0);
        assertThat(v.id()).isEqualTo("color-contrast");
        assertThat(v.impact()).isEqualTo("serious");
        assertThat(v.isBlocking()).isTrue();
        assertThat(v.nodeCount()).isEqualTo(2);
        assertThat(v.nodeTargets()).containsExactly("#login .field", "button.submit");
    }

    @Test
    @DisplayName("AxeRunner.parse returns empty list when there are no violations")
    void parseHandlesEmptyViolations() {
        assertThat(AxeRunner.parse("{\"violations\": []}")).isEmpty();
    }
}
