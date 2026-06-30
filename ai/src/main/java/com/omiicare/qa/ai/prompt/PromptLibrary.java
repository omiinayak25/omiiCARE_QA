package com.omiicare.qa.ai.prompt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Loads versioned prompt templates from the classpath ({@code /prompts/<name>.txt}).
 * The human-facing prompt library lives under {@code ai/prompts/} as Markdown; the
 * engine ships machine-loadable copies as resources so prompts are reproducible.
 */
public final class PromptLibrary {

    private PromptLibrary() {}

    public static PromptTemplate load(String name) {
        String resource = "/prompts/" + name + ".txt";
        try (InputStream in = PromptLibrary.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("Prompt template not found: " + resource);
            }
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return new PromptTemplate(name, body);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read prompt template: " + resource, e);
        }
    }
}
