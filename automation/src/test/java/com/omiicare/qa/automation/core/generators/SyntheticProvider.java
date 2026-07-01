package com.omiicare.qa.automation.core.generators;

/**
 * An immutable, PHI-safe synthetic healthcare provider (practitioner) used as test data.
 *
 * <p>All values are fictional and must never represent a real clinician. Identifiers use
 * non-routable / test domains so the data cannot leak into production systems.
 *
 * @param firstName provider given name
 * @param lastName provider family name
 * @param gender administrative gender (MALE | FEMALE | OTHER | UNKNOWN)
 * @param npi 10-digit National Provider Identifier (synthetic, not Luhn-validated against the real
 *     registry)
 * @param specialty clinical specialty label (e.g. "Cardiology")
 * @param email non-routable contact email
 * @param phone fictional contact phone in the reserved +1-555 range
 */
public record SyntheticProvider(
        String firstName,
        String lastName,
        String gender,
        String npi,
        String specialty,
        String email,
        String phone) {}
