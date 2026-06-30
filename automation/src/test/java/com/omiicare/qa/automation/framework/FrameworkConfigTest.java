package com.omiicare.qa.automation.framework;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import org.junit.jupiter.api.Test;

/** Framework unit test: configuration resolution and system-property override. */
class FrameworkConfigTest {

    @Test
    void resolvesDefaultBaseUriForKnownSystem() {
        FrameworkConfig config = FrameworkConfig.get();
        assertThat(config.baseUri(TargetSystem.DUMMYJSON)).isEqualTo("https://dummyjson.com");
    }

    @Test
    void systemPropertyOverridesDefaultBaseUri() {
        String key = TargetSystem.RESTFUL_BOOKER.baseUriProperty();
        System.setProperty(key, "http://localhost:9999");
        try {
            assertThat(FrameworkConfig.get().baseUri(TargetSystem.RESTFUL_BOOKER))
                    .isEqualTo("http://localhost:9999");
        } finally {
            System.clearProperty(key);
        }
    }
}
