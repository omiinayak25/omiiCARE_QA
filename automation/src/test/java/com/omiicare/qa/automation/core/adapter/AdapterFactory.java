package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Resolves a {@link ResourceAdapter} for a {@link TargetSystem} (Factory pattern). Most systems use
 * the generic {@link HttpResourceAdapter}; register a custom supplier here when a target needs
 * special handling — existing tests are unaffected.
 */
public final class AdapterFactory {

    private static final Map<
                    TargetSystem, BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter>>
            REGISTRY = new EnumMap<>(TargetSystem.class);

    private AdapterFactory() {}

    public static void register(
            TargetSystem system,
            BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter> supplier) {
        REGISTRY.put(system, supplier);
    }

    public static ResourceAdapter create(TargetSystem system) {
        return create(system, FrameworkConfig.get());
    }

    public static ResourceAdapter create(TargetSystem system, FrameworkConfig config) {
        BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter> supplier =
                REGISTRY.getOrDefault(system, HttpResourceAdapter::new);
        return supplier.apply(system, config);
    }
}
