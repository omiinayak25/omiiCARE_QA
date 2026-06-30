package com.omiicare.qa.ai.assistant;

import com.omiicare.qa.ai.config.AiConfig;
import com.omiicare.qa.ai.prompt.PromptLibrary;
import com.omiicare.qa.ai.prompt.PromptTemplate;
import com.omiicare.qa.ai.provider.AiProvider;
import com.omiicare.qa.ai.provider.AiProviderFactory;
import com.omiicare.qa.ai.provider.AiRequest;
import com.omiicare.qa.ai.provider.AiResponse;
import com.omiicare.qa.ai.security.PromptGuardrails;
import java.util.Map;

/**
 * AI-assisted failure analysis: given test logs / stack traces, it builds a
 * reviewed prompt and asks the configured provider for a probable root cause,
 * evidence, suggested next steps, and a confidence level. The result is always
 * marked AI-assisted and intended for human review — it never auto-acts.
 *
 * <p>Behavior respects {@link AiConfig}: when AI is disabled the assistant returns
 * a clear "disabled" notice instead of calling any provider, and input is screened
 * by {@link PromptGuardrails} so secrets are never transmitted.
 */
public class FailureAnalysisAssistant {

    private final AiConfig config;
    private final AiProvider provider;

    public FailureAnalysisAssistant(AiConfig config) {
        this(config, AiProviderFactory.create(config));
    }

    public FailureAnalysisAssistant(AiConfig config, AiProvider provider) {
        this.config = config;
        this.provider = provider;
    }

    public AiResponse analyze(String testName, String logsAndStackTrace) {
        PromptGuardrails.assertSafe(logsAndStackTrace);
        if (!config.isEnabled()) {
            return new AiResponse(
                    "AI is disabled (set omii.ai.enabled=true to use AI-assisted analysis). "
                            + "No provider was called.",
                    "none",
                    config.model(),
                    false);
        }
        PromptTemplate template = PromptLibrary.load("failure-analysis");
        String prompt =
                template.render(
                        Map.of(
                                "testName", testName == null ? "(unknown)" : testName,
                                "logs", logsAndStackTrace == null ? "" : logsAndStackTrace));
        return provider.complete(
                new AiRequest(
                        "You are a senior SDET. Analyze test failures factually. Never fabricate.",
                        prompt,
                        1024));
    }
}
