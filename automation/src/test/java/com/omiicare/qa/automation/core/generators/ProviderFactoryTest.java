package com.omiicare.qa.automation.core.generators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for {@link ProviderFactory}. No SUT, browser, network, or DB required —
 * these validate the shape and determinism of generated synthetic data only.
 */
class ProviderFactoryTest {

    @Test
    @DisplayName("newProvider() populates all PHI-safe fields with valid values")
    void producesValidProvider() {
        SyntheticProvider p = new ProviderFactory(42L).newProvider();

        assertThat(p.firstName()).isNotBlank();
        assertThat(p.lastName()).isNotBlank();
        assertThat(p.gender()).isIn("MALE", "FEMALE", "OTHER", "UNKNOWN");
        assertThat(p.specialty()).isNotBlank();
        // Synthetic NPI: exactly 10 digits.
        assertThat(p.npi()).hasSize(10).containsOnlyDigits();
        // Contact data must be non-routable test data.
        assertThat(p.email()).endsWith("@providers.example").doesNotContain(" ");
        assertThat(p.phone()).startsWith("+1-555");
    }

    @Test
    @DisplayName("same seed yields identical providers (deterministic)")
    void deterministicForSameSeed() {
        SyntheticProvider a = ProviderFactory.seeded(7L).newProvider();
        SyntheticProvider b = ProviderFactory.seeded(7L).newProvider();
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("different seeds eventually diverge")
    void differentSeedsDiverge() {
        List<SyntheticProvider> seedOne = new ArrayList<>();
        List<SyntheticProvider> seedTwo = new ArrayList<>();
        ProviderFactory f1 = new ProviderFactory(1L);
        ProviderFactory f2 = new ProviderFactory(2L);
        for (int i = 0; i < 10; i++) {
            seedOne.add(f1.newProvider());
            seedTwo.add(f2.newProvider());
        }
        assertThat(seedOne).isNotEqualTo(seedTwo);
    }
}
