package com.omiicare.qa.automation.hl7;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Structural and content validator for HL7 v2 messages plus ACK/NAK generation.
 *
 * <p>The validator performs a layered set of checks:
 *
 * <ul>
 *   <li><b>Header</b> — MSH present and first; field/encoding characters; mandatory MSH-9/10/11/12.
 *   <li><b>Required segments</b> — PID for ADT/ORU; OBR/OBX for ORU.
 *   <li><b>Required fields</b> — PID-3 (patient id) and PID-5 (name).
 *   <li><b>Datatypes</b> — DOB (PID-7) and MSH-7 timestamps are valid HL7 TS values; sex code in the
 *       HL7 table 0001 value set.
 * </ul>
 *
 * <p>Validation never throws on a malformed-but-parsed message; it accumulates problems into a
 * {@link Result}. Only programmer errors (null input) throw.
 */
public final class Hl7Validator {

    /** A single validation problem with a severity and human-readable location/detail. */
    public static final class Problem {
        /** Severity classes used by the adversarial quality gate. */
        public enum Severity {
            ERROR,
            WARNING
        }

        private final Severity severity;
        private final String location;
        private final String message;

        public Problem(Severity severity, String location, String message) {
            this.severity = severity;
            this.location = location;
            this.message = message;
        }

        public Severity severity() {
            return severity;
        }

        public String location() {
            return location;
        }

        public String message() {
            return message;
        }

        @Override
        public String toString() {
            return "[" + severity + "] " + location + ": " + message;
        }
    }

    /** Outcome of validating a message: the collected problems and a convenience valid flag. */
    public static final class Result {
        private final List<Problem> problems;

        Result(List<Problem> problems) {
            this.problems = Collections.unmodifiableList(new ArrayList<>(problems));
        }

        /** True when there are no ERROR-severity problems (warnings are tolerated). */
        public boolean isValid() {
            return problems.stream().noneMatch(p -> p.severity() == Problem.Severity.ERROR);
        }

        public List<Problem> problems() {
            return problems;
        }

        public List<Problem> errors() {
            return problems.stream()
                    .filter(p -> p.severity() == Problem.Severity.ERROR)
                    .toList();
        }

        public List<Problem> warnings() {
            return problems.stream()
                    .filter(p -> p.severity() == Problem.Severity.WARNING)
                    .toList();
        }

        @Override
        public String toString() {
            return "Result[valid=" + isValid() + ", problems=" + problems.size() + "]";
        }
    }

    private static final DateTimeFormatter TS_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TS_DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** HL7 table 0001 administrative sex codes. */
    private static final List<String> VALID_SEX_CODES = List.of("A", "F", "M", "N", "O", "U");

    /**
     * Validates the supplied parsed message.
     *
     * @param message a parsed HL7 message; must not be null
     * @return the accumulated validation result
     */
    public Result validate(Hl7Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message must not be null");
        }
        List<Problem> problems = new ArrayList<>();

        validateHeader(message, problems);
        validatePatientIdentity(message, problems);
        validateMessageTypeSpecifics(message, problems);

        return new Result(problems);
    }

    private void validateHeader(Hl7Message message, List<Problem> problems) {
        List<Hl7Message.Segment> segs = message.segments();
        if (segs.isEmpty() || !"MSH".equals(segs.get(0).id())) {
            problems.add(
                    new Problem(
                            Problem.Severity.ERROR,
                            "MSH",
                            "MSH segment must be present and the first segment of the message"));
            return;
        }
        Hl7Message.Segment msh = segs.get(0);

        // MSH-1 field separator must be a single char (the standard pipe is recommended).
        String fieldSep = msh.field(1);
        if (fieldSep.length() != 1) {
            problems.add(
                    new Problem(Problem.Severity.ERROR, "MSH-1", "Field separator must be a single character"));
        }

        // MSH-2 encoding characters: expect 4 (component, repetition, escape, sub-component).
        String enc = msh.field(2);
        if (enc.length() < 4) {
            problems.add(
                    new Problem(
                            Problem.Severity.ERROR,
                            "MSH-2",
                            "Encoding characters must declare component, repetition, escape and sub-component"));
        }

        requireField(msh, 9, "MSH-9", "Message type", problems);
        requireField(msh, 10, "MSH-10", "Message control id", problems);
        requireField(msh, 11, "MSH-11", "Processing id", problems);
        requireField(msh, 12, "MSH-12", "Version id", problems);

        // MSH-9 must carry a message-code component.
        String msgCode = msh.component(9, 1, message.componentSeparator());
        if (msgCode.isEmpty()) {
            problems.add(
                    new Problem(Problem.Severity.ERROR, "MSH-9.1", "Message type code (e.g. ADT, ORU) is required"));
        }

        // MSH-7 message datetime: optional but, if present, must be a valid TS.
        String mshDateTime = msh.field(7);
        if (!mshDateTime.isEmpty() && !isValidTimestamp(mshDateTime)) {
            problems.add(
                    new Problem(
                            Problem.Severity.WARNING,
                            "MSH-7",
                            "Message datetime '" + mshDateTime + "' is not a valid HL7 timestamp"));
        }
    }

    private void validatePatientIdentity(Hl7Message message, List<Problem> problems) {
        Hl7Message.Segment pid = message.firstSegment("PID");
        if (pid == null) {
            problems.add(new Problem(Problem.Severity.ERROR, "PID", "PID segment is required"));
            return;
        }

        String patientId = pid.field(3);
        if (patientId.isEmpty()) {
            problems.add(
                    new Problem(Problem.Severity.ERROR, "PID-3", "Patient identifier list is required"));
        }

        String name = pid.field(5);
        if (name.isEmpty()) {
            problems.add(new Problem(Problem.Severity.ERROR, "PID-5", "Patient name is required"));
        } else {
            String family = pid.component(5, 1, message.componentSeparator());
            if (family.isEmpty()) {
                problems.add(
                        new Problem(Problem.Severity.ERROR, "PID-5.1", "Patient family name is required"));
            }
        }

        // PID-7 DOB datatype check (optional field, but if present must be a valid date).
        String dob = pid.field(7);
        if (!dob.isEmpty() && !isValidDate(dob)) {
            problems.add(
                    new Problem(
                            Problem.Severity.ERROR,
                            "PID-7",
                            "Date of birth '" + dob + "' is not a valid HL7 date (yyyyMMdd)"));
        }

        // PID-8 administrative sex code-set check.
        String sex = pid.field(8);
        if (!sex.isEmpty() && !VALID_SEX_CODES.contains(sex)) {
            problems.add(
                    new Problem(
                            Problem.Severity.WARNING,
                            "PID-8",
                            "Administrative sex '" + sex + "' is not in HL7 table 0001 " + VALID_SEX_CODES));
        }
    }

    private void validateMessageTypeSpecifics(Hl7Message message, List<Problem> problems) {
        Hl7Message.Segment msh = message.firstSegment("MSH");
        if (msh == null) {
            return;
        }
        String code = msh.component(9, 1, message.componentSeparator());
        if ("ORU".equals(code)) {
            if (!message.hasSegment("OBR")) {
                problems.add(new Problem(Problem.Severity.ERROR, "OBR", "ORU message requires an OBR segment"));
            }
            if (!message.hasSegment("OBX")) {
                problems.add(
                        new Problem(Problem.Severity.ERROR, "OBX", "ORU message requires at least one OBX segment"));
            } else {
                int idx = 1;
                for (Hl7Message.Segment obx : message.segments("OBX")) {
                    if (obx.field(2).isEmpty()) {
                        problems.add(
                                new Problem(
                                        Problem.Severity.ERROR,
                                        "OBX[" + idx + "]-2",
                                        "Observation value type is required"));
                    }
                    if (obx.field(3).isEmpty()) {
                        problems.add(
                                new Problem(
                                        Problem.Severity.ERROR,
                                        "OBX[" + idx + "]-3",
                                        "Observation identifier is required"));
                    }
                    idx++;
                }
            }
        }
    }

    private void requireField(
            Hl7Message.Segment seg, int index, String loc, String label, List<Problem> problems) {
        if (seg.field(index).isEmpty()) {
            problems.add(new Problem(Problem.Severity.ERROR, loc, label + " is required"));
        }
    }

    private static boolean isValidDate(String value) {
        if (value.length() != 8) {
            return false;
        }
        try {
            TS_DATE.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isValidTimestamp(String value) {
        // Accept a date-only (8) or full datetime (14) HL7 TS; strip any timezone offset.
        String core = value;
        int plus = core.indexOf('+');
        int minus = core.indexOf('-');
        int tz = plus >= 0 ? plus : minus;
        if (tz > 0) {
            core = core.substring(0, tz);
        }
        if (core.length() == 8) {
            return isValidDate(core);
        }
        if (core.length() == 14) {
            try {
                TS_DATETIME.parse(core);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Builds an HL7 ACK (acknowledgement) message in response to an inbound message.
     *
     * <p>Per HL7, the ACK echoes the original MSH-10 in MSA-2 and carries an acknowledgement code in
     * MSA-1: {@code AA} (Application Accept) for a valid message or {@code AE} (Application Error)
     * otherwise. Use {@link #buildNak(Hl7Message, String)} to force a rejection.
     *
     * @param original the message being acknowledged
     * @param result the validation result that determines AA vs AE
     * @return a raw ACK message string
     */
    public String buildAck(Hl7Message original, Result result) {
        String ackCode = result.isValid() ? "AA" : "AE";
        String detail =
                result.isValid()
                        ? ""
                        : result.errors().isEmpty() ? "" : result.errors().get(0).toString();
        return buildAcknowledgement(original, ackCode, detail);
    }

    /**
     * Builds an HL7 NAK (negative acknowledgement) with code {@code AR} (Application Reject).
     *
     * @param original the message being rejected
     * @param reason free-text rejection reason placed in MSA-3
     * @return a raw NAK message string
     */
    public String buildNak(Hl7Message original, String reason) {
        return buildAcknowledgement(original, "AR", reason == null ? "" : reason);
    }

    private String buildAcknowledgement(Hl7Message original, String ackCode, String textMessage) {
        char field = original.fieldSeparator();
        char comp = original.componentSeparator();
        Hl7Message.Segment msh = original.firstSegment("MSH");

        String sendingApp = "OMIICARE_QA";
        String sendingFac = "OMII_LAB";
        String receivingApp = "";
        String receivingFac = "";
        String controlId = "";
        String processingId = "P";
        String version = "2.5";
        if (msh != null) {
            // The ACK swaps sender/receiver from the original message.
            receivingApp = msh.field(3);
            receivingFac = msh.field(4);
            sendingApp = emptyToDefault(msh.field(5), sendingApp);
            sendingFac = emptyToDefault(msh.field(6), sendingFac);
            controlId = msh.field(10);
            processingId = emptyToDefault(msh.field(11), processingId);
            version = emptyToDefault(msh.field(12), version);
        }
        String ts = java.time.LocalDateTime.now().format(TS_DATETIME);
        String ackControlId = "ACK" + (controlId.isEmpty() ? ts : controlId);

        StringBuilder sb = new StringBuilder();
        sb.append("MSH")
                .append(field)
                .append("" + comp + '~' + '\\' + '&')
                .append(field)
                .append(sendingApp)
                .append(field)
                .append(sendingFac)
                .append(field)
                .append(receivingApp)
                .append(field)
                .append(receivingFac)
                .append(field)
                .append(ts)
                .append(field) // MSH-8 security
                .append(field)
                .append("ACK") // MSH-9 message type
                .append(field)
                .append(ackControlId) // MSH-10
                .append(field)
                .append(processingId) // MSH-11
                .append(field)
                .append(version) // MSH-12
                .append('\r');
        sb.append("MSA")
                .append(field)
                .append(ackCode) // MSA-1 acknowledgement code
                .append(field)
                .append(controlId) // MSA-2 message control id
                .append(field)
                .append(textMessage) // MSA-3 text message
                .append('\r');
        return sb.toString();
    }

    private static String emptyToDefault(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }
}
