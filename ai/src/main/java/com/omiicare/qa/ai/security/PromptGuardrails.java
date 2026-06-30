package com.omiicare.qa.ai.security;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Security guardrails for AI input. AI must never receive or transmit secrets or PHI. {@link
 * #assertSafe} rejects prompts that contain credential-like material; {@link #redact} masks them
 * defensively. Healthcare data passed to AI must be synthetic and PHI-safe — this is the last line
 * of defense, not a substitute for not sending sensitive data in the first place.
 */
public final class PromptGuardrails {

    private static final List<Pattern> SECRET_PATTERNS =
            List.of(
                    Pattern.compile(
                            "(?i)(api[_-]?key|secret|password|passwd|token)\\s*[:=]\\s*\\S+"),
                    Pattern.compile("-----BEGIN [A-Z ]*PRIVATE KEY-----"),
                    Pattern.compile(
                            "eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}"),
                    Pattern.compile("(?i)bearer\\s+[A-Za-z0-9._-]{20,}"),
                    Pattern.compile("AKIA[0-9A-Z]{16}"));

    private PromptGuardrails() {}

    public static boolean containsSecret(String text) {
        if (text == null) {
            return false;
        }
        return SECRET_PATTERNS.stream().anyMatch(p -> p.matcher(text).find());
    }

    public static void assertSafe(String text) {
        if (containsSecret(text)) {
            throw new GuardrailViolationException(
                    "Prompt appears to contain a secret or credential; refusing to send to an AI"
                            + " provider. Remove the sensitive value or use a synthetic placeholder.");
        }
    }

    public static String redact(String text) {
        if (text == null) {
            return null;
        }
        String redacted = text;
        for (Pattern p : SECRET_PATTERNS) {
            redacted = p.matcher(redacted).replaceAll("[REDACTED]");
        }
        return redacted;
    }
}
