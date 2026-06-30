package com.omiicare.qa.automation.core.generators;

/** An immutable, PHI-safe synthetic patient used as test data. */
public record SyntheticPatient(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String email,
        String phone) {}
