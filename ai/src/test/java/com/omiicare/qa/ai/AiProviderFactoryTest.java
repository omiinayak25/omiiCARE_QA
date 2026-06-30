package com.omiicare.qa.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.omiicare.qa.ai.config.AiConfig;
import com.omiicare.qa.ai.provider.AiProvider;
import com.omiicare.qa.ai.provider.AiProviderFactory;
import com.omiicare.qa.ai.provider.AiRequest;
import org.junit.jupiter.api.Test;

class AiProviderFactoryTest {

    @Test
    void defaultsToLocalProviderOffline() {
        AiProvider provider = AiProviderFactory.create(AiConfig.of(false, "local", "m", ""));
        assertThat(provider.name()).isEqualTo("local");
        assertThat(provider.complete(new AiRequest("sys", "hello", 256)).aiAssisted()).isTrue();
    }

    @Test
    void hostedProviderWithoutKeyFailsFastWithoutNetworkCall() {
        AiProvider provider =
                AiProviderFactory.create(AiConfig.of(true, "claude", "claude-opus-4-8", ""));
        assertThatThrownBy(() -> provider.complete(new AiRequest(null, "hi", 64)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("API key");
    }

    @Test
    void rejectsUnknownProvider() {
        assertThatThrownBy(() -> AiProviderFactory.create(AiConfig.of(false, "acme", "m", "")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
