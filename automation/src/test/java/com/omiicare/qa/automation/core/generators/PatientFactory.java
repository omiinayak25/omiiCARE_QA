package com.omiicare.qa.automation.core.generators;

import java.time.LocalDate;
import java.util.Locale;
import net.datafaker.Faker;

/**
 * Produces realistic, PHI-safe synthetic patients (Factory/Builder pattern over
 * Datafaker). All data is fictional — never real patient information. Output is
 * deterministic per-call only in field shape, not value, which suits fresh test
 * data generation.
 */
public class PatientFactory {

    private static final String[] GENDERS = {"MALE", "FEMALE", "OTHER", "UNKNOWN"};

    private final Faker faker;

    public PatientFactory() {
        this(new Faker(Locale.ENGLISH));
    }

    public PatientFactory(Faker faker) {
        this.faker = faker;
    }

    public SyntheticPatient newPatient() {
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        LocalDate dob =
                LocalDate.now().minusYears(faker.number().numberBetween(1, 95));
        String gender = GENDERS[faker.number().numberBetween(0, GENDERS.length)];
        // Use the example.com / test domain to keep contact data non-routable.
        String email =
                (first + "." + last + "@demo.example").toLowerCase(Locale.ROOT).replace(" ", "");
        String phone = faker.numerify("+1-555-0#-###");
        return new SyntheticPatient(first, last, dob.toString(), gender, email, phone);
    }
}
