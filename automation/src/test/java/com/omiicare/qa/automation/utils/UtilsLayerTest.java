package com.omiicare.qa.automation.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Unit tests for the Utility layer. No SUT — runs in the default build. */
class UtilsLayerTest {

    @Test
    void waitUntilReturnsTrueWhenConditionHolds() {
        assertThat(WaitUtils.waitUntil(() -> true, Duration.ofSeconds(1))).isTrue();
    }

    @Test
    void waitUntilTimesOutWhenConditionNeverHolds() {
        assertThat(WaitUtils.waitUntil(() -> false, Duration.ofMillis(200), Duration.ofMillis(50)))
                .isFalse();
    }

    @Test
    void dateUtilsAgeAndIso() {
        LocalDate dob = LocalDate.of(2000, 1, 1);
        assertThat(DateUtils.ageInYears(dob, LocalDate.of(2020, 1, 1))).isEqualTo(20);
        assertThat(DateUtils.iso(dob)).isEqualTo("2000-01-01");
        assertThat(DateUtils.parseIso("1990-12-31")).isEqualTo(LocalDate.of(1990, 12, 31));
    }

    @Test
    void stringUtils() {
        assertThat(StringUtils.isBlank("  ")).isTrue();
        assertThat(StringUtils.isNotBlank("x")).isTrue();
        assertThat(StringUtils.normalize("  a   b ")).isEqualTo("a b");
        assertThat(StringUtils.padId(7, 4)).isEqualTo("0007");
        assertThat(StringUtils.containsIgnoreCase("Hello", "ELL")).isTrue();
    }

    @Test
    void randomDataUtils() {
        assertThat(RandomDataUtils.uniqueSuffix()).isNotEqualTo(RandomDataUtils.uniqueSuffix());
        assertThat(RandomDataUtils.randomAlphanumeric(6)).hasSize(6);
        int n = RandomDataUtils.randomInt(1, 3);
        assertThat(n).isBetween(1, 3);
    }
}
