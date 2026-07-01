package com.omiicare.qa.automation.hl7;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for the HL7 v2 validation framework.
 *
 * <p>These tests are intentionally <b>untagged</b>: they exercise only the in-memory builder,
 * parser and validator. They require no SUT, no browser, no network and no database, are fully
 * deterministic, and therefore run as part of the default {@code mvn test} build.
 */
@DisplayName("HL7 v2 validation framework")
class Hl7ValidationFrameworkTest {

    @Nested
    @DisplayName("Hl7MessageBuilder + Hl7Message round-trip")
    class BuilderParseTests {

        @Test
        @DisplayName("ADT^A04 builds and parses into the expected segments and fields")
        void adtA04RoundTrips() {
            String raw =
                    Hl7MessageBuilder.adtA04()
                            .messageControlId("MSGCTRL001")
                            .patientId("OMII-1001")
                            .patientName("Doe", "Jane")
                            .dateOfBirth("19900215")
                            .administrativeSex("F")
                            .build();

            Hl7Message message = Hl7Message.parse(raw);

            assertThat(message.fieldSeparator()).isEqualTo('|');
            assertThat(message.componentSeparator()).isEqualTo('^');
            assertThat(message.messageType()).isEqualTo("ADT^A04");
            assertThat(message.hasSegment("MSH")).isTrue();
            assertThat(message.hasSegment("EVN")).isTrue();
            assertThat(message.hasSegment("PID")).isTrue();

            Hl7Message.Segment msh = message.firstSegment("MSH");
            assertThat(msh.field(1)).isEqualTo("|");
            assertThat(msh.field(2)).isEqualTo("^~\\&");
            assertThat(msh.field(10)).isEqualTo("MSGCTRL001");
            assertThat(msh.field(12)).isEqualTo("2.5");

            Hl7Message.Segment pid = message.firstSegment("PID");
            assertThat(pid.field(3)).isEqualTo("OMII-1001");
            assertThat(pid.component(5, 1, '^')).isEqualTo("Doe");
            assertThat(pid.component(5, 2, '^')).isEqualTo("Jane");
            assertThat(pid.field(7)).isEqualTo("19900215");
            assertThat(pid.field(8)).isEqualTo("F");
        }

        @Test
        @DisplayName("ORU^R01 includes OBR and OBX observation rows")
        void oruR01IncludesObservations() {
            Hl7Message message =
                    Hl7MessageBuilder.oruR01()
                            .messageControlId("ORU123")
                            .patientId("OMII-2002")
                            .patientName("Smith", "John")
                            .dateOfBirth("19850101")
                            .administrativeSex("M")
                            .addObservation("NM", "718-7^Hemoglobin^LN", "13.5", "g/dL")
                            .addObservation("NM", "789-8^Erythrocytes^LN", "4.7", "10*6/uL")
                            .buildMessage();

            assertThat(message.messageType()).isEqualTo("ORU^R01");
            assertThat(message.hasSegment("OBR")).isTrue();
            assertThat(message.segments("OBX")).hasSize(2);

            Hl7Message.Segment firstObx = message.segments("OBX").get(0);
            assertThat(firstObx.field(2)).isEqualTo("NM");
            assertThat(firstObx.field(5)).isEqualTo("13.5");
            assertThat(firstObx.field(6)).isEqualTo("g/dL");
        }
    }

    @Nested
    @DisplayName("Hl7Validator")
    class ValidatorTests {

        private final Hl7Validator validator = new Hl7Validator();

        @Test
        @DisplayName("A well-formed ADT^A04 passes validation with no errors")
        void validAdtPasses() {
            Hl7Message message =
                    Hl7MessageBuilder.adtA04()
                            .patientId("OMII-1001")
                            .patientName("Doe", "Jane")
                            .dateOfBirth("19900215")
                            .administrativeSex("F")
                            .buildMessage();

            Hl7Validator.Result result = validator.validate(message);

            assertThat(result.isValid()).isTrue();
            assertThat(result.errors()).isEmpty();
        }

        @Test
        @DisplayName("Missing patient identity yields PID-3 and family-name errors")
        void missingPatientIdentityFails() {
            // No patient id and an empty family/given name. PID-5 is present as a bare component
            // separator, so the validator flags the missing family component (PID-5.1) plus PID-3.
            Hl7Message message =
                    Hl7MessageBuilder.adtA04().patientName("", "").buildMessage();

            Hl7Validator.Result result = validator.validate(message);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors())
                    .extracting(Hl7Validator.Problem::location)
                    .contains("PID-3", "PID-5.1");
        }

        @Test
        @DisplayName("Completely absent PID-5 (no name at all) yields a PID-5 error")
        void absentNameFails() {
            String raw =
                    "MSH|^~\\&|OMIICARE_QA|OMII_LAB|OPENMRS|OMII_HOSP|20260701120000||ADT^A04|C1|P|2.5\r"
                            + "PID|1|||OMII-9009\r";
            Hl7Validator.Result result = validator.validate(Hl7Message.parse(raw));

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors())
                    .extracting(Hl7Validator.Problem::location)
                    .contains("PID-5");
        }

        @Test
        @DisplayName("Invalid date of birth datatype is flagged")
        void invalidDobFlagged() {
            // Build a valid skeleton, then corrupt PID-7 to an impossible date.
            String raw =
                    Hl7MessageBuilder.adtA04()
                            .patientId("OMII-1001")
                            .patientName("Doe", "Jane")
                            .dateOfBirth("19901301") // month 13 is invalid
                            .administrativeSex("F")
                            .build();

            Hl7Validator.Result result = validator.validate(Hl7Message.parse(raw));

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors())
                    .extracting(Hl7Validator.Problem::location)
                    .contains("PID-7");
        }

        @Test
        @DisplayName("ORU without OBX is rejected")
        void oruWithoutObxRejected() {
            // Hand-craft an ORU lacking OBR/OBX to bypass the builder's guard.
            String raw =
                    "MSH|^~\\&|OMIICARE_QA|OMII_LAB|OPENMRS|OMII_HOSP|20260701120000||ORU^R01|CTRL9|P|2.5\r"
                            + "PID|1|||OMII-3003||Roe^Richard||19751120|M\r";
            Hl7Message message = Hl7Message.parse(raw);

            Hl7Validator.Result result = validator.validate(message);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors())
                    .extracting(Hl7Validator.Problem::location)
                    .contains("OBR", "OBX");
        }

        @Test
        @DisplayName("Message lacking MSH header is rejected")
        void missingMshRejected() {
            Hl7Message message = Hl7Message.parse("PID|1|||OMII-1|Doe^Jane||19900215|F");

            Hl7Validator.Result result = validator.validate(message);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors())
                    .extracting(Hl7Validator.Problem::location)
                    .contains("MSH");
        }

        @Test
        @DisplayName("ACK uses AA for valid messages and AR for forced NAK, echoing the control id")
        void ackAndNakGeneration() {
            Hl7Message message =
                    Hl7MessageBuilder.adtA04()
                            .messageControlId("CTRL777")
                            .patientId("OMII-1001")
                            .patientName("Doe", "Jane")
                            .dateOfBirth("19900215")
                            .administrativeSex("F")
                            .buildMessage();
            Hl7Validator.Result result = validator.validate(message);

            String ack = validator.buildAck(message, result);
            Hl7Message ackMessage = Hl7Message.parse(ack);
            assertThat(ackMessage.firstSegment("MSA").field(1)).isEqualTo("AA");
            assertThat(ackMessage.firstSegment("MSA").field(2)).isEqualTo("CTRL777");

            String nak = validator.buildNak(message, "Rejected for test");
            Hl7Message nakMessage = Hl7Message.parse(nak);
            assertThat(nakMessage.firstSegment("MSA").field(1)).isEqualTo("AR");
            assertThat(nakMessage.firstSegment("MSA").field(2)).isEqualTo("CTRL777");
            assertThat(nakMessage.firstSegment("MSA").field(3)).isEqualTo("Rejected for test");
        }

        @Test
        @DisplayName("Non-standard sex code produces a warning but not an error")
        void nonStandardSexWarns() {
            String raw =
                    Hl7MessageBuilder.adtA04()
                            .patientId("OMII-1001")
                            .patientName("Doe", "Jane")
                            .dateOfBirth("19900215")
                            .administrativeSex("Z") // not in HL7 table 0001
                            .build();

            Hl7Validator.Result result = validator.validate(Hl7Message.parse(raw));

            assertThat(result.isValid()).isTrue(); // warning only
            assertThat(result.warnings())
                    .extracting(Hl7Validator.Problem::location)
                    .contains("PID-8");
        }
    }
}
