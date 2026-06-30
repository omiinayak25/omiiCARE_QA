package com.omiicare.qa.patient.api;

import java.time.LocalDate;

/** API representation of a patient. */
public record PatientResponse(
        Long id,
        String mrn,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phone,
        String status) {}
