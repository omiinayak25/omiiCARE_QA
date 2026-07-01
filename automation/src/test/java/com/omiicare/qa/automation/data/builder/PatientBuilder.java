package com.omiicare.qa.automation.data.builder;

import com.omiicare.qa.automation.data.model.PatientData;
import com.omiicare.qa.automation.utils.DateUtils;
import com.omiicare.qa.automation.utils.RandomDataUtils;
import java.time.LocalDate;
import net.datafaker.Faker;

/**
 * Fluent {@link PatientData} builder (Data layer — Builder pattern). Start from
 * {@link #randomValid()} for a complete PHI-safe patient, then override only what a test cares
 * about. All names carry a unique suffix so registration never collides with shared demo data.
 */
public final class PatientBuilder {

    private static final Faker FAKER = new Faker();

    private String givenName;
    private String middleName;
    private String familyName;
    private String gender = "M";
    private LocalDate birthDate = DateUtils.birthDateForAge(30);
    private String addressLine;
    private String city;
    private String phone;

    private PatientBuilder() {}

    /** A fresh, empty builder. */
    public static PatientBuilder aPatient() {
        return new PatientBuilder();
    }

    /** A builder pre-populated with a complete, valid, unique, PHI-safe synthetic patient. */
    public static PatientBuilder randomValid() {
        String suffix = RandomDataUtils.uniqueSuffix();
        return aPatient()
                .withGivenName(FAKER.name().firstName())
                .withFamilyName("Qa" + suffix)
                .withGender(RandomDataUtils.randomInt(0, 1) == 0 ? "M" : "F")
                .withBirthDate(DateUtils.birthDateForAge(RandomDataUtils.randomInt(1, 90)))
                .withAddressLine(FAKER.address().streetAddress())
                .withCity(FAKER.address().city())
                .withPhone(FAKER.phoneNumber().subscriberNumber(10));
    }

    public PatientBuilder withGivenName(String v) {
        this.givenName = v;
        return this;
    }

    public PatientBuilder withMiddleName(String v) {
        this.middleName = v;
        return this;
    }

    public PatientBuilder withFamilyName(String v) {
        this.familyName = v;
        return this;
    }

    /** @param code {@code "M"} or {@code "F"} */
    public PatientBuilder withGender(String code) {
        this.gender = code;
        return this;
    }

    public PatientBuilder withBirthDate(LocalDate v) {
        this.birthDate = v;
        return this;
    }

    public PatientBuilder withAge(int years) {
        this.birthDate = DateUtils.birthDateForAge(years);
        return this;
    }

    public PatientBuilder withAddressLine(String v) {
        this.addressLine = v;
        return this;
    }

    public PatientBuilder withCity(String v) {
        this.city = v;
        return this;
    }

    public PatientBuilder withPhone(String v) {
        this.phone = v;
        return this;
    }

    /** Builds an immutable {@link PatientData}, validating required fields. */
    public PatientData build() {
        require(givenName, "givenName");
        require(familyName, "familyName");
        require(gender, "gender");
        if (birthDate == null) {
            throw new IllegalStateException("birthDate is required");
        }
        return new PatientData(
                givenName, middleName, familyName, gender, birthDate, addressLine, city, phone);
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(field + " is required");
        }
    }
}
