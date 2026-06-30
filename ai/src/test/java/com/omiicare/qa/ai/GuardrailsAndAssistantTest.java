package com.omiicare.qa.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.omiicare.qa.ai.assistant.FailureAnalysisAssistant;
import com.omiicare.qa.ai.config.AiConfig;
import com.omiicare.qa.ai.provider.AiResponse;
import com.omiicare.qa.ai.security.GuardrailViolationException;
import com.omiicare.qa.ai.security.PromptGuardrails;
import org.junit.jupiter.api.Test;

class GuardrailsAndAssistantTest {

    @Test
    void guardrailDetectsAndRedactsSecrets() {
        String withSecret = "config: api_key=sk-supersecretvalue123456";
        assertThat(PromptGuardrails.containsSecret(withSecret)).isTrue();
        assertThat(PromptGuardrails.redact(withSecret)).contains("[REDACTED]");
        assertThatThrownBy(() -> PromptGuardrails.assertSafe(withSecret))
                .isInstanceOf(GuardrailViolationException.class);
    }

    @Test
    void disabledAssistantReturnsNoticeWithoutProviderCall() {
        FailureAnalysisAssistant assistant =
                new FailureAnalysisAssistant(AiConfig.of(false, "local", "m", ""));
        AiResponse response =
                assistant.analyze("LoginUiE2ETest", "TimeoutError waiting for selector");
        assertThat(response.aiAssisted()).isFalse();
        assertThat(response.text()).contains("disabled");
    }

    @Test
    void enabledLocalAssistantRendersPromptAndReturnsAiAssisted() {
        FailureAnalysisAssistant assistant =
                new FailureAnalysisAssistant(AiConfig.of(true, "local", "local-echo", ""));
        AiResponse response =
                assistant.analyze("PatientApiE2ETest", "AssertionError: expected 201 but was 409");
        assertThat(response.aiAssisted()).isTrue();
        assertThat(response.text()).contains("PatientApiE2ETest");
    }

    @Test
    void assistantRefusesInputContainingSecret() {
        FailureAnalysisAssistant assistant =
                new FailureAnalysisAssistant(AiConfig.of(true, "local", "m", ""));
        assertThatThrownBy(
                        () ->
                                assistant.analyze(
                                        "T",
                                        "Authorization: Bearer abcdefghijklmnopqrstuvwxyz123456"))
                .isInstanceOf(GuardrailViolationException.class);
    }
}
