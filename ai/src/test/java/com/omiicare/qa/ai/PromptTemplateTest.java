package com.omiicare.qa.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.omiicare.qa.ai.prompt.PromptTemplate;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PromptTemplateTest {

    @Test
    void substitutesVariables() {
        PromptTemplate t = new PromptTemplate("greet", "Hello {{name}}, env={{env}}");
        assertThat(t.render(Map.of("name", "Ada", "env", "qa"))).isEqualTo("Hello Ada, env=qa");
    }

    @Test
    void failsFastOnMissingVariable() {
        PromptTemplate t = new PromptTemplate("greet", "Hello {{name}}");
        assertThatThrownBy(() -> t.render(Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }
}
