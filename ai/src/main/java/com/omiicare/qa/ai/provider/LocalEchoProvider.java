package com.omiicare.qa.ai.provider;

/**
 * A deterministic, network-free provider used for offline development, tests, and demos. It does
 * not call any external service; it returns a structured echo that makes the prompt wiring
 * observable without consuming tokens or credentials.
 */
public class LocalEchoProvider implements AiProvider {

    private final String model;

    public LocalEchoProvider(String model) {
        this.model = model == null || model.isBlank() ? "local-echo" : model;
    }

    @Override
    public String name() {
        return "local";
    }

    @Override
    public AiResponse complete(AiRequest request) {
        String text =
                "[AI-ASSISTED · local-echo · review required]\n"
                        + "system: "
                        + (request.systemPrompt() == null ? "" : request.systemPrompt())
                        + "\nresponse-to: "
                        + request.userPrompt();
        return AiResponse.of(text, name(), model);
    }
}
