package com.omiicare.qa.ai.provider;

/**
 * Provider abstraction. Concrete providers (Claude, OpenAI, local, future) are selected by
 * configuration and never coupled into business logic. Implementations must be safe to construct
 * offline; only {@link #complete} may require network access and credentials.
 */
public interface AiProvider {

    /** A stable provider name (e.g. {@code claude}, {@code openai}, {@code local}). */
    String name();

    /** Produces a completion for the request. May throw if credentials are absent. */
    AiResponse complete(AiRequest request);
}
