package com.omiicare.qa.automation.core.generators;

import java.util.Locale;
import java.util.Random;
import net.datafaker.Faker;

/**
 * Produces realistic, PHI-safe synthetic {@link SyntheticProvider providers} (Factory pattern over
 * Datafaker).
 *
 * <p>All generated data is fictional and must never represent a real clinician. NPIs use a synthetic
 * 10-digit shape and contact details use non-routable test domains / the reserved +1-555 phone
 * range.
 *
 * <p><strong>Determinism:</strong> construct with {@link #ProviderFactory(long)} (or {@link
 * #seeded(long)}) to obtain a repeatable stream of providers — identical seeds yield identical
 * output, which is essential for reproducible test fixtures. The no-arg constructor produces fresh
 * (non-deterministic) data.
 */
public class ProviderFactory {

    private static final String[] GENDERS = {"MALE", "FEMALE", "OTHER", "UNKNOWN"};

    private static final String[] SPECIALTIES = {
        "Cardiology",
        "Dermatology",
        "Family Medicine",
        "Internal Medicine",
        "Neurology",
        "Obstetrics & Gynecology",
        "Oncology",
        "Orthopedics",
        "Pediatrics",
        "Psychiatry",
        "Radiology"
    };

    private final Faker faker;

    /** Creates a factory backed by a non-deterministic Datafaker instance. */
    public ProviderFactory() {
        this(new Faker(Locale.ENGLISH));
    }

    /**
     * Creates a deterministic factory: the same {@code seed} always yields the same sequence of
     * providers.
     *
     * @param seed pseudo-random seed
     */
    public ProviderFactory(long seed) {
        this(new Faker(Locale.ENGLISH, new Random(seed)));
    }

    /**
     * Creates a factory backed by a caller-supplied Datafaker instance (advanced / shared-seed use).
     *
     * @param faker non-null Datafaker instance
     */
    public ProviderFactory(Faker faker) {
        this.faker = faker;
    }

    /**
     * Convenience factory for a deterministic, seeded instance.
     *
     * @param seed pseudo-random seed
     * @return a seeded {@link ProviderFactory}
     */
    public static ProviderFactory seeded(long seed) {
        return new ProviderFactory(seed);
    }

    /**
     * Generates a single synthetic provider.
     *
     * @return a fully-populated, PHI-safe {@link SyntheticProvider}
     */
    public SyntheticProvider newProvider() {
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        String gender = GENDERS[faker.number().numberBetween(0, GENDERS.length)];
        String specialty = SPECIALTIES[faker.number().numberBetween(0, SPECIALTIES.length)];
        // 10-digit synthetic NPI; first digit 1-9 to avoid a leading zero being dropped downstream.
        String npi = faker.number().numberBetween(1, 10) + faker.numerify("#########");
        String email =
                (first + "." + last + "@providers.example")
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "");
        String phone = faker.numerify("+1-555-0#-###");
        return new SyntheticProvider(first, last, gender, npi, specialty, email, phone);
    }
}
