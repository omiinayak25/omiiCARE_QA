package com.omiicare.qa.patient.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Payload to register a new patient. MRN is optional (auto-generated if absent). */
public record CreatePatientRequest(
        @Size(max = 40) String mrn,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull @Past(message = "date of birth must be in the past") LocalDate dateOfBirth,
        @NotBlank String gender,
        @Email String email,
        @Size(max = 40) String phone) {}
