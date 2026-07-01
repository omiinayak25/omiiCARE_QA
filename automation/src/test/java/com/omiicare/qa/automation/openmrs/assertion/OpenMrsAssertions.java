package com.omiicare.qa.automation.openmrs.assertion;

import com.microsoft.playwright.Page;
import io.restassured.response.Response;

/**
 * Single entry point for OpenMRS assertions (Assertion layer — Facade). Aggregates the UI, login,
 * patient and API assertion families; the specialised {@code fhir.FhirAssertions},
 * {@code db.DbAssertions} and {@code a11y.A11yAssertions} classes remain the FHIR / database /
 * accessibility assertion surfaces.
 */
public final class OpenMrsAssertions {

    private OpenMrsAssertions() {}

    public static UiAssertions ui(Page page) {
        return UiAssertions.on(page);
    }

    public static LoginAssertions login(Page page) {
        return LoginAssertions.on(page);
    }

    public static PatientAssertions patient(Page page) {
        return PatientAssertions.on(page);
    }

    public static ApiAssertions api(Response response) {
        return ApiAssertions.on(response);
    }
}
