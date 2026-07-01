package com.omiicare.qa.automation.core.generators;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;
import net.datafaker.Faker;

/**
 * Produces PHI-safe synthetic {@link SyntheticOrder clinical orders} (lab / drug / imaging) using
 * the Factory pattern over Datafaker.
 *
 * <p>Codes and displays are illustrative test values, not authoritative terminology bindings. Every
 * order carries a valid lifecycle status, a priority, and an ISO-8601 UTC activation instant.
 *
 * <p><strong>Determinism:</strong> use {@link #OrderFactory(long)} / {@link #seeded(long)} for a
 * repeatable stream of orders.
 */
public class OrderFactory {

    private static final String[] ORDER_TYPES = {"DRUG", "LAB", "IMAGING"};

    private static final String[][] CATALOG = {
        // {orderType, code, display}
        {"LAB", "CBC", "Complete Blood Count"},
        {"LAB", "BMP", "Basic Metabolic Panel"},
        {"LAB", "HBA1C", "Hemoglobin A1c"},
        {"LAB", "LIPID", "Lipid Panel"},
        {"DRUG", "AMOX500", "Amoxicillin 500 mg capsule"},
        {"DRUG", "LISINO10", "Lisinopril 10 mg tablet"},
        {"DRUG", "METFOR500", "Metformin 500 mg tablet"},
        {"IMAGING", "CXR", "Chest X-ray"},
        {"IMAGING", "CTHEAD", "CT Head without contrast"},
        {"IMAGING", "USABD", "Abdominal Ultrasound"}
    };

    private static final String[] STATUSES = {"ACTIVE", "ON_HOLD", "COMPLETED", "CANCELLED"};

    private static final String[] PRIORITIES = {"ROUTINE", "URGENT", "STAT"};

    private final Faker faker;
    private final Instant referenceNow;

    /** Creates a factory backed by a non-deterministic Datafaker instance. */
    public OrderFactory() {
        this(new Faker(Locale.ENGLISH), Instant.now());
    }

    /**
     * Creates a deterministic factory: the same {@code seed} yields the same sequence of orders,
     * anchored to a fixed reference instant.
     *
     * @param seed pseudo-random seed
     */
    public OrderFactory(long seed) {
        this(new Faker(Locale.ENGLISH, new Random(seed)), Instant.EPOCH.plus(20_000, ChronoUnit.DAYS));
    }

    /**
     * Creates a factory backed by a caller-supplied Datafaker instance and reference instant.
     *
     * @param faker non-null Datafaker instance
     * @param referenceNow the "now" instant relative to which activation time is set
     */
    public OrderFactory(Faker faker, Instant referenceNow) {
        this.faker = faker;
        this.referenceNow = referenceNow;
    }

    /**
     * Convenience factory for a deterministic, seeded instance.
     *
     * @param seed pseudo-random seed
     * @return a seeded {@link OrderFactory}
     */
    public static OrderFactory seeded(long seed) {
        return new OrderFactory(seed);
    }

    /**
     * Generates a single synthetic order for the given subject references.
     *
     * @param patientRef logical patient reference (e.g. "Patient/example")
     * @param ordererRef logical ordering-provider reference (e.g. "Practitioner/example")
     * @return a fully-populated {@link SyntheticOrder}
     */
    public SyntheticOrder newOrder(String patientRef, String ordererRef) {
        String[] item = CATALOG[faker.number().numberBetween(0, CATALOG.length)];
        String status = STATUSES[faker.number().numberBetween(0, STATUSES.length)];
        String priority = PRIORITIES[faker.number().numberBetween(0, PRIORITIES.length)];

        // Activated between 0 and 7 days before the reference instant, truncated to minutes.
        long minutesAgo = faker.number().numberBetween(0L, 7L * 24L * 60L);
        Instant activated =
                referenceNow.minus(minutesAgo, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);

        return new SyntheticOrder(
                patientRef, ordererRef, item[0], item[1], item[2], status, priority, activated.toString());
    }

    /**
     * Generates an order using placeholder example references.
     *
     * @return a fully-populated {@link SyntheticOrder}
     */
    public SyntheticOrder newOrder() {
        return newOrder("Patient/example", "Practitioner/example");
    }
}
