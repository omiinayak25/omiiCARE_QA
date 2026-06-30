package com.omiicare.qa.ai.prompt;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A reusable prompt template with {@code {{variable}}} placeholders. Rendering
 * substitutes provided variables; an unresolved placeholder is a programming error
 * (fail fast) so prompts are never silently sent with missing context.
 */
public final class PromptTemplate {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*}}");

    private final String name;
    private final String body;

    public PromptTemplate(String name, String body) {
        this.name = name;
        this.body = body;
    }

    public String name() {
        return name;
    }

    public String render(Map<String, String> variables) {
        Matcher matcher = PLACEHOLDER.matcher(body);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = variables.get(key);
            if (value == null) {
                throw new IllegalArgumentException(
                        "Missing value for prompt variable '" + key + "' in template '" + name + "'");
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
