package com.omiicare.qa.automation.core.generators;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;
import net.datafaker.Faker;

/**
 * Produces PHI-safe synthetic {@link SyntheticAppointment appointments} (Factory pattern over
 * Datafaker).
 *
 * <p>Each appointment links a patient and a practitioner reference, carries a valid lifecycle
 * status, and has an {@code end} strictly after {@code start} with a matching {@code
 * minutesDuration}. Timestamps are emitted as ISO-8601 UTC instants.
 *
 * <p><strong>Determinism:</strong> use {@link #AppointmentFactory(long)} / {@link #seeded(long)} for
 * a repeatable stream. A fixed reference {@code now} is also captured at construction time so that
 * generated start/end instants are stable for a seeded factory.
 */
public class AppointmentFactory {

    private static final String[] STATUSES = {
        "PROPOSED", "PENDING", "BOOKED", "ARRIVED", "FULFILLED", "CANCELLED", "NOSHOW"
    };

    private static final String[] DESCRIPTIONS = {
        "Routine follow-up visit",
        "Initial consultation",
        "Annual physical examination",
        "Medication review",
        "Post-operative check"
    };

    private static final int[] DURATIONS = {15, 30, 45, 60};

    private final Faker faker;
    private final Instant referenceNow;

    /** Creates a factory backed by a non-deterministic Datafaker instance. */
    public AppointmentFactory() {
        this(new Faker(Locale.ENGLISH), Instant.now());
    }

    /**
     * Creates a deterministic factory: the same {@code seed} yields the same sequence of
     * appointments, anchored to a fixed reference instant.
     *
     * @param seed pseudo-random seed
     */
    public AppointmentFactory(long seed) {
        this(new Faker(Locale.ENGLISH, new Random(seed)), Instant.EPOCH.plus(20_000, ChronoUnit.DAYS));
    }

    /**
     * Creates a factory backed by a caller-supplied Datafaker instance and reference instant.
     *
     * @param faker non-null Datafaker instance
     * @param referenceNow the "now" instant relative to which appointment times are scheduled
     */
    public AppointmentFactory(Faker faker, Instant referenceNow) {
        this.faker = faker;
        this.referenceNow = referenceNow;
    }

    /**
     * Convenience factory for a deterministic, seeded instance.
     *
     * @param seed pseudo-random seed
     * @return a seeded {@link AppointmentFactory}
     */
    public static AppointmentFactory seeded(long seed) {
        return new AppointmentFactory(seed);
    }

    /**
     * Generates a single synthetic appointment for the given subject references.
     *
     * @param patientRef logical patient reference (e.g. "Patient/example")
     * @param practitionerRef logical practitioner reference (e.g. "Practitioner/example")
     * @return a fully-populated {@link SyntheticAppointment} with end strictly after start
     */
    public SyntheticAppointment newAppointment(String patientRef, String practitionerRef) {
        String status = STATUSES[faker.number().numberBetween(0, STATUSES.length)];
        String description = DESCRIPTIONS[faker.number().numberBetween(0, DESCRIPTIONS.length)];
        int minutes = DURATIONS[faker.number().numberBetween(0, DURATIONS.length)];

        // Schedule between 1 and 30 days out, truncated to whole minutes for clean payloads.
        long offsetMinutes = faker.number().numberBetween(24L * 60L, 30L * 24L * 60L);
        Instant start = referenceNow.plus(offsetMinutes, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);
        Instant end = start.plus(Duration.ofMinutes(minutes));

        return new SyntheticAppointment(
                patientRef, practitionerRef, status, start.toString(), end.toString(), minutes, description);
    }

    /**
     * Generates an appointment using placeholder example references.
     *
     * @return a fully-populated {@link SyntheticAppointment}
     */
    public SyntheticAppointment newAppointment() {
        return newAppointment("Patient/example", "Practitioner/example");
    }
}
