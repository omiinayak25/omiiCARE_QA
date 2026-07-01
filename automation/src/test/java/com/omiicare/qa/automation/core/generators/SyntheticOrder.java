package com.omiicare.qa.automation.core.generators;

/**
 * An immutable, PHI-safe synthetic clinical order (e.g. lab or drug order) used as test data.
 *
 * <p>All codes are illustrative test values, not authoritative terminology bindings.
 *
 * @param patientRef logical reference to the subject patient (e.g. "Patient/example")
 * @param ordererRef logical reference to the ordering provider (e.g. "Practitioner/example")
 * @param orderType order category (e.g. DRUG | LAB | IMAGING)
 * @param code order/test code (illustrative)
 * @param display human-readable description of the ordered item
 * @param status order lifecycle status (e.g. ACTIVE | COMPLETED | CANCELLED)
 * @param priority order urgency (e.g. ROUTINE | URGENT | STAT)
 * @param dateActivated ISO-8601 UTC instant the order was activated
 */
public record SyntheticOrder(
        String patientRef,
        String ordererRef,
        String orderType,
        String code,
        String display,
        String status,
        String priority,
        String dateActivated) {}
