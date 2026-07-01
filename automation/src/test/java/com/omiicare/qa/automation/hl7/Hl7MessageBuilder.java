package com.omiicare.qa.automation.hl7;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fluent builder that assembles syntactically valid HL7 v2.5 messages.
 *
 * <p>The builder focuses on the two message types exercised by the OMII healthcare automation
 * suite:
 *
 * <ul>
 *   <li>{@code ADT^A04} — Register a patient (admission/registration).
 *   <li>{@code ORU^R01} — Unsolicited observation result.
 * </ul>
 *
 * <p>It owns delimiter and MSH bookkeeping so callers only supply business data. The produced
 * string uses {@code \r} segment terminators (the HL7 standard) and standard delimiters
 * {@code |^~\&}. Output is round-trippable through {@link Hl7Message#parse(String)}.
 */
public final class Hl7MessageBuilder {

    private static final char FIELD = '|';
    private static final char COMPONENT = '^';
    private static final char REPETITION = '~';
    private static final char ESCAPE = '\\';
    private static final char SUBCOMPONENT = '&';
    private static final String ENCODING_CHARS = "" + COMPONENT + REPETITION + ESCAPE + SUBCOMPONENT;
    private static final String SEGMENT_TERMINATOR = "\r";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** Monotonic fallback for message control ids so each built message is unique within a JVM. */
    private static final AtomicLong CONTROL_SEQUENCE = new AtomicLong(1);

    private String sendingApplication = "OMIICARE_QA";
    private String sendingFacility = "OMII_LAB";
    private String receivingApplication = "OPENMRS";
    private String receivingFacility = "OMII_HOSP";
    private String messageControlId;
    private String processingId = "P"; // P=Production, T=Training, D=Debug
    private String versionId = "2.5";
    private LocalDateTime messageDateTime = LocalDateTime.now();

    // Patient (PID) fields.
    private String patientId = "";
    private String familyName = "";
    private String givenName = "";
    private String dateOfBirth = ""; // yyyyMMdd
    private String administrativeSex = ""; // M, F, O, U, A, N

    // Event (EVN / observation) fields.
    private String eventTypeCode = ""; // e.g. A04 or R01

    // OBR/OBX payload for ORU messages.
    private final List<String[]> observations = new ArrayList<>();

    private Hl7MessageBuilder() {}

    /** Starts an {@code ADT^A04} (register patient) message builder. */
    public static Hl7MessageBuilder adtA04() {
        Hl7MessageBuilder b = new Hl7MessageBuilder();
        b.eventTypeCode = "A04";
        return b;
    }

    /** Starts an {@code ORU^R01} (observation result) message builder. */
    public static Hl7MessageBuilder oruR01() {
        Hl7MessageBuilder b = new Hl7MessageBuilder();
        b.eventTypeCode = "R01";
        return b;
    }

    public Hl7MessageBuilder sendingApplication(String value) {
        this.sendingApplication = value;
        return this;
    }

    public Hl7MessageBuilder sendingFacility(String value) {
        this.sendingFacility = value;
        return this;
    }

    public Hl7MessageBuilder receivingApplication(String value) {
        this.receivingApplication = value;
        return this;
    }

    public Hl7MessageBuilder receivingFacility(String value) {
        this.receivingFacility = value;
        return this;
    }

    public Hl7MessageBuilder messageControlId(String value) {
        this.messageControlId = value;
        return this;
    }

    public Hl7MessageBuilder processingId(String value) {
        this.processingId = value;
        return this;
    }

    public Hl7MessageBuilder versionId(String value) {
        this.versionId = value;
        return this;
    }

    public Hl7MessageBuilder messageDateTime(LocalDateTime value) {
        this.messageDateTime = Objects.requireNonNull(value, "messageDateTime");
        return this;
    }

    public Hl7MessageBuilder patientId(String value) {
        this.patientId = value;
        return this;
    }

    /** Sets patient name as family/given components of PID-5. */
    public Hl7MessageBuilder patientName(String family, String given) {
        this.familyName = family;
        this.givenName = given;
        return this;
    }

    /** Date of birth in HL7 {@code yyyyMMdd} form (PID-7). */
    public Hl7MessageBuilder dateOfBirth(String yyyymmdd) {
        this.dateOfBirth = yyyymmdd;
        return this;
    }

    /** Administrative sex code (PID-8): one of M, F, O, U, A, N. */
    public Hl7MessageBuilder administrativeSex(String code) {
        this.administrativeSex = code;
        return this;
    }

    /**
     * Adds an OBX observation row for an ORU message.
     *
     * @param valueType OBX-2 value type (e.g. NM, ST, CE)
     * @param identifier OBX-3 observation identifier (e.g. {@code 718-7^Hemoglobin^LN})
     * @param value OBX-5 observation value
     * @param units OBX-6 units (e.g. {@code g/dL})
     */
    public Hl7MessageBuilder addObservation(
            String valueType, String identifier, String value, String units) {
        observations.add(new String[] {valueType, identifier, value, units});
        return this;
    }

    /**
     * Builds the raw HL7 message string.
     *
     * @return a complete, parseable HL7 v2 message
     * @throws IllegalStateException if the builder is asked to produce ORU output with no OBX rows
     */
    public String build() {
        String controlId =
                (messageControlId != null && !messageControlId.isBlank())
                        ? messageControlId
                        : "OMII" + CONTROL_SEQUENCE.getAndIncrement();
        String ts = messageDateTime.format(TS);
        String messageType = "R01".equals(eventTypeCode) ? "ORU" : "ADT";

        StringBuilder sb = new StringBuilder();
        // ---- MSH ----
        sb.append("MSH")
                .append(FIELD)
                .append(ENCODING_CHARS)
                .append(FIELD)
                .append(sendingApplication)
                .append(FIELD)
                .append(sendingFacility)
                .append(FIELD)
                .append(receivingApplication)
                .append(FIELD)
                .append(receivingFacility)
                .append(FIELD)
                .append(ts)
                .append(FIELD) // MSH-7 datetime; MSH-8 security empty
                .append(FIELD)
                .append(messageType)
                .append(COMPONENT)
                .append(eventTypeCode) // MSH-9 message type
                .append(FIELD)
                .append(controlId) // MSH-10
                .append(FIELD)
                .append(processingId) // MSH-11
                .append(FIELD)
                .append(versionId) // MSH-12
                .append(SEGMENT_TERMINATOR);

        // ---- EVN (event type) ----
        sb.append("EVN")
                .append(FIELD)
                .append(eventTypeCode)
                .append(FIELD)
                .append(ts)
                .append(SEGMENT_TERMINATOR);

        // ---- PID ----
        sb.append("PID")
                .append(FIELD)
                .append("1") // PID-1 set id
                .append(FIELD) // PID-2 (deprecated patient id)
                .append(FIELD)
                .append(patientId) // PID-3 patient identifier list
                .append(FIELD) // PID-4 alternate id
                .append(FIELD)
                .append(familyName)
                .append(COMPONENT)
                .append(givenName) // PID-5 name
                .append(FIELD) // PID-6 mother maiden
                .append(FIELD)
                .append(dateOfBirth) // PID-7 dob
                .append(FIELD)
                .append(administrativeSex) // PID-8 sex
                .append(SEGMENT_TERMINATOR);

        if ("R01".equals(eventTypeCode)) {
            if (observations.isEmpty()) {
                throw new IllegalStateException("ORU^R01 requires at least one OBX observation");
            }
            // ---- OBR ----
            sb.append("OBR")
                    .append(FIELD)
                    .append("1") // OBR-1 set id
                    .append(FIELD) // OBR-2 placer order number
                    .append(FIELD) // OBR-3 filler order number
                    .append(FIELD)
                    .append("BATTERY") // OBR-4 universal service id
                    .append(SEGMENT_TERMINATOR);
            // ---- OBX rows ----
            int setId = 1;
            for (String[] obs : observations) {
                sb.append("OBX")
                        .append(FIELD)
                        .append(setId++) // OBX-1 set id
                        .append(FIELD)
                        .append(nullToEmpty(obs[0])) // OBX-2 value type
                        .append(FIELD)
                        .append(nullToEmpty(obs[1])) // OBX-3 observation id
                        .append(FIELD) // OBX-4 sub id
                        .append(FIELD)
                        .append(nullToEmpty(obs[2])) // OBX-5 value
                        .append(FIELD)
                        .append(nullToEmpty(obs[3])) // OBX-6 units
                        .append(FIELD) // OBX-7 ref range
                        .append(FIELD) // OBX-8 abnormal flags
                        .append(FIELD) // OBX-9 probability
                        .append(FIELD) // OBX-10 nature
                        .append(FIELD)
                        .append("F") // OBX-11 result status = Final
                        .append(SEGMENT_TERMINATOR);
            }
        }

        return sb.toString();
    }

    /** Builds and immediately parses the message for fluent test assertions. */
    public Hl7Message buildMessage() {
        return Hl7Message.parse(build());
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
