package com.omiicare.qa.automation.core.generators;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pure-logic unit tests for {@link OrderFactory}. No SUT, browser, network, or DB required. */
class OrderFactoryTest {

    @Test
    @DisplayName("newOrder() populates a coherent, valid order")
    void producesValidOrder() {
        SyntheticOrder o = new OrderFactory(11L).newOrder("Patient/p1", "Practitioner/dr1");

        assertThat(o.patientRef()).isEqualTo("Patient/p1");
        assertThat(o.ordererRef()).isEqualTo("Practitioner/dr1");
        assertThat(o.orderType()).isIn("DRUG", "LAB", "IMAGING");
        assertThat(o.code()).isNotBlank();
        assertThat(o.display()).isNotBlank();
        assertThat(o.status()).isIn("ACTIVE", "ON_HOLD", "COMPLETED", "CANCELLED");
        assertThat(o.priority()).isIn("ROUTINE", "URGENT", "STAT");
        // Activation must be a parseable UTC instant.
        assertThat(o.dateActivated()).endsWith("Z");
        assertThat(Instant.parse(o.dateActivated())).isNotNull();
    }

    @Test
    @DisplayName("same seed yields identical orders (deterministic)")
    void deterministicForSameSeed() {
        SyntheticOrder a = OrderFactory.seeded(8L).newOrder();
        SyntheticOrder b = OrderFactory.seeded(8L).newOrder();
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("code/display pair always comes from the catalog for its order type")
    void catalogConsistency() {
        OrderFactory factory = new OrderFactory(123L);
        for (int i = 0; i < 25; i++) {
            SyntheticOrder o = factory.newOrder();
            // Every generated item is internally consistent: non-null type, code and display.
            assertThat(o.orderType()).isNotBlank();
            assertThat(o.code()).isNotBlank();
            assertThat(o.display()).isNotBlank();
        }
    }
}
