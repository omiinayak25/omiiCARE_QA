package com.omiicare.qa.automation.hl7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable, self-contained parser/model for an HL7 v2.x message.
 *
 * <p>An HL7 v2 message is a sequence of <em>segments</em> separated by the segment terminator
 * (carriage return {@code \r}; this parser is lenient and also accepts {@code \n} and {@code \r\n}).
 * Each segment is a list of <em>fields</em> separated by the field separator (conventionally
 * {@code |}). Fields may carry <em>components</em> separated by the component separator
 * (conventionally {@code ^}). The actual delimiters are declared by the {@code MSH} segment itself:
 * MSH-1 is the field separator (the 4th character of the raw message) and MSH-2 carries the
 * encoding characters (component, repetition, escape, sub-component).
 *
 * <p>This class deliberately depends only on the JDK so it can be used anywhere on the test
 * classpath without third-party HL7 libraries.
 */
public final class Hl7Message {

    /** Default HL7 delimiters used when a message has no parseable MSH header. */
    public static final char DEFAULT_FIELD_SEPARATOR = '|';

    public static final char DEFAULT_COMPONENT_SEPARATOR = '^';
    public static final char DEFAULT_REPETITION_SEPARATOR = '~';
    public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
    public static final char DEFAULT_SUBCOMPONENT_SEPARATOR = '&';

    /** A single parsed segment: its 3-letter id plus its raw, ordered field strings. */
    public static final class Segment {
        private final String id;
        private final List<String> fields;

        Segment(String id, List<String> fields) {
            this.id = id;
            this.fields = Collections.unmodifiableList(new ArrayList<>(fields));
        }

        /** The 3-character segment identifier, e.g. {@code MSH}, {@code PID}. */
        public String id() {
            return id;
        }

        /**
         * Returns field {@code index} using HL7 positional numbering.
         *
         * <p>HL7 numbers fields starting at 1, where for the {@code MSH} segment MSH-1 is the field
         * separator character and MSH-2 the encoding characters. For every other segment, field 0 is
         * the segment id and field 1 is the first data field. This accessor follows the HL7
         * convention: {@code field(1)} on a PID returns PID-1, and {@code field(1)} on MSH returns the
         * field-separator character.
         *
         * @return the field value, or empty string if the position is absent
         */
        public String field(int index) {
            if ("MSH".equals(id)) {
                // fields list = [MSH, <sep>, <enc>, MSH-3, ...]; MSH-1 == separator at list pos 1.
                int pos = index;
                return pos >= 0 && pos < fields.size() ? fields.get(pos) : "";
            }
            // fields list = [PID, PID-1, PID-2, ...]; PID-n at list pos n.
            return index >= 0 && index < fields.size() ? fields.get(index) : "";
        }

        /**
         * Returns the 1-based component of a field, splitting on the component separator.
         *
         * @param fieldIndex HL7 field number
         * @param componentIndex 1-based component number
         * @param componentSeparator delimiter to split components
         * @return the component, or empty string when absent
         */
        public String component(int fieldIndex, int componentIndex, char componentSeparator) {
            String raw = field(fieldIndex);
            if (raw.isEmpty() || componentIndex < 1) {
                return "";
            }
            String[] parts = raw.split(java.util.regex.Pattern.quote(String.valueOf(componentSeparator)), -1);
            return componentIndex <= parts.length ? parts[componentIndex - 1] : "";
        }

        /** Number of fields recorded for this segment (including the id slot). */
        public int fieldCount() {
            return fields.size();
        }

        /** Raw, unmodifiable view of the underlying field strings. */
        public List<String> rawFields() {
            return fields;
        }

        @Override
        public String toString() {
            return id + "(" + fields.size() + " fields)";
        }
    }

    private final List<Segment> segments;
    private final Map<String, List<Segment>> byId;
    private final char fieldSeparator;
    private final char componentSeparator;
    private final char repetitionSeparator;
    private final char escapeCharacter;
    private final char subComponentSeparator;

    private Hl7Message(
            List<Segment> segments,
            char fieldSeparator,
            char componentSeparator,
            char repetitionSeparator,
            char escapeCharacter,
            char subComponentSeparator) {
        this.segments = Collections.unmodifiableList(new ArrayList<>(segments));
        this.fieldSeparator = fieldSeparator;
        this.componentSeparator = componentSeparator;
        this.repetitionSeparator = repetitionSeparator;
        this.escapeCharacter = escapeCharacter;
        this.subComponentSeparator = subComponentSeparator;
        Map<String, List<Segment>> index = new LinkedHashMap<>();
        for (Segment s : this.segments) {
            index.computeIfAbsent(s.id(), k -> new ArrayList<>()).add(s);
        }
        this.byId = Collections.unmodifiableMap(index);
    }

    /**
     * Parses a raw HL7 v2 message string into a structured {@link Hl7Message}.
     *
     * <p>Delimiters are auto-detected from the MSH header when present. If the message does not begin
     * with a valid MSH header, HL7 default delimiters are assumed so that callers can still inspect a
     * malformed message (validation, not parsing, is responsible for rejecting it).
     *
     * @param raw the raw message; must be non-null
     * @return a parsed message
     * @throws IllegalArgumentException if {@code raw} is null or blank
     */
    public static Hl7Message parse(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Raw HL7 message must not be null");
        }
        String trimmed = raw.strip();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Raw HL7 message must not be blank");
        }

        char fieldSep = DEFAULT_FIELD_SEPARATOR;
        char compSep = DEFAULT_COMPONENT_SEPARATOR;
        char repSep = DEFAULT_REPETITION_SEPARATOR;
        char escChar = DEFAULT_ESCAPE_CHARACTER;
        char subSep = DEFAULT_SUBCOMPONENT_SEPARATOR;

        if (trimmed.startsWith("MSH") && trimmed.length() > 3) {
            fieldSep = trimmed.charAt(3);
            // Encoding characters live immediately after the field separator: comp/rep/esc/sub.
            int encStart = 4;
            String enc = "";
            int nextSep = trimmed.indexOf(fieldSep, encStart);
            if (nextSep > encStart) {
                enc = trimmed.substring(encStart, nextSep);
            } else if (nextSep == -1 && trimmed.length() > encStart) {
                enc = trimmed.substring(encStart);
            }
            if (enc.length() >= 1) {
                compSep = enc.charAt(0);
            }
            if (enc.length() >= 2) {
                repSep = enc.charAt(1);
            }
            if (enc.length() >= 3) {
                escChar = enc.charAt(2);
            }
            if (enc.length() >= 4) {
                subSep = enc.charAt(3);
            }
        }

        List<Segment> segments = new ArrayList<>();
        // Split on any flavour of line terminator used by HL7 sources.
        String[] lines = trimmed.split("\\r\\n|\\r|\\n", -1);
        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }
            segments.add(parseSegment(line, fieldSep));
        }

        return new Hl7Message(segments, fieldSep, compSep, repSep, escChar, subSep);
    }

    private static Segment parseSegment(String line, char fieldSep) {
        String quoted = java.util.regex.Pattern.quote(String.valueOf(fieldSep));
        if (line.startsWith("MSH")) {
            // MSH is special: the field separator is itself MSH-1, so it must be re-inserted as a
            // field. We split the remainder (after "MSH" + separator) and prepend [MSH, <sep>].
            List<String> fields = new ArrayList<>();
            fields.add("MSH");
            fields.add(String.valueOf(fieldSep));
            String rest = line.length() > 4 ? line.substring(4) : "";
            if (!rest.isEmpty()) {
                String[] parts = rest.split(quoted, -1);
                Collections.addAll(fields, parts);
            }
            return new Segment("MSH", fields);
        }
        String[] parts = line.split(quoted, -1);
        List<String> fields = new ArrayList<>();
        Collections.addAll(fields, parts);
        String id = parts.length > 0 ? parts[0] : "";
        return new Segment(id, fields);
    }

    /** All segments, in message order. */
    public List<Segment> segments() {
        return segments;
    }

    /** Returns the first segment with the given id, or {@code null} if none exists. */
    public Segment firstSegment(String id) {
        List<Segment> list = byId.get(id);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    /** Returns all segments with the given id, never null. */
    public List<Segment> segments(String id) {
        return byId.getOrDefault(id, Collections.emptyList());
    }

    /** True if at least one segment with the given id is present. */
    public boolean hasSegment(String id) {
        return byId.containsKey(id) && !byId.get(id).isEmpty();
    }

    /** The detected or defaulted field separator. */
    public char fieldSeparator() {
        return fieldSeparator;
    }

    /** The detected or defaulted component separator. */
    public char componentSeparator() {
        return componentSeparator;
    }

    /** The detected or defaulted repetition separator. */
    public char repetitionSeparator() {
        return repetitionSeparator;
    }

    /** The detected or defaulted escape character. */
    public char escapeCharacter() {
        return escapeCharacter;
    }

    /** The detected or defaulted sub-component separator. */
    public char subComponentSeparator() {
        return subComponentSeparator;
    }

    /**
     * Convenience: the message type as {@code MSG^EVENT} from MSH-9, e.g. {@code ADT^A04}. Returns
     * empty string when MSH or MSH-9 is absent.
     */
    public String messageType() {
        Segment msh = firstSegment("MSH");
        if (msh == null) {
            return "";
        }
        String type = msh.component(9, 1, componentSeparator);
        String event = msh.component(9, 2, componentSeparator);
        if (type.isEmpty()) {
            return "";
        }
        return event.isEmpty() ? type : type + componentSeparator + event;
    }

    @Override
    public String toString() {
        return "Hl7Message[" + messageType() + ", " + segments.size() + " segments]";
    }
}
