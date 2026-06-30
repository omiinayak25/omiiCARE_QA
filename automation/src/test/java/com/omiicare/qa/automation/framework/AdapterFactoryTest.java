package com.omiicare.qa.automation.framework;

import static org.assertj.core.api.Assertions.assertThat;

import com.omiicare.qa.automation.core.adapter.AdapterFactory;
import com.omiicare.qa.automation.core.adapter.ResourceAdapter;
import com.omiicare.qa.automation.core.config.TargetSystem;
import org.junit.jupiter.api.Test;

/** Framework unit test: adapter resolution and URL composition. */
class AdapterFactoryTest {

    @Test
    void createsHttpAdapterForEachTargetByDefault() {
        for (TargetSystem system : TargetSystem.values()) {
            ResourceAdapter adapter = AdapterFactory.create(system);
            assertThat(adapter.system()).isEqualTo(system);
            assertThat(adapter.baseUri()).isNotBlank();
        }
    }

    @Test
    void composesUrlWithoutDoubleSlash() {
        ResourceAdapter adapter = AdapterFactory.create(TargetSystem.DUMMYJSON);
        assertThat(adapter.url("/products/1")).isEqualTo("https://dummyjson.com/products/1");
        assertThat(adapter.url("products/1")).isEqualTo("https://dummyjson.com/products/1");
    }
}
