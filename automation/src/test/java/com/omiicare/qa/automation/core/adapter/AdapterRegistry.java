package com.omiicare.qa.automation.core.adapter;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import com.omiicare.qa.automation.core.config.TargetSystem;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Central wiring helper that binds each {@link TargetSystem} to its specialized
 * {@link ResourceAdapter} supplier and resolves adapters on demand.
 *
 * <p>This complements {@link AdapterFactory}: where the factory is a low-level registry of
 * suppliers, {@code AdapterRegistry} provides the curated, framework-known mapping of the systems
 * the platform supports (OpenMRS, OpenEMR, HAPI FHIR, omiiCARE) and a single
 * {@link #installInto()} hook that registers all of them with the factory.
 *
 * <p>The default supplier set is immutable for the framework-known systems; any system without a
 * specialized adapter falls back to the generic {@link HttpResourceAdapter}. The class holds no
 * mutable singleton state, so it is safe to use from parallel tests.
 */
public final class AdapterRegistry {

    /** Curated mapping of framework-known systems to their specialized adapter suppliers. */
    private static final Map<
                    TargetSystem, BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter>>
            SPECIALIZED = buildSpecialized();

    private AdapterRegistry() {}

    private static Map<TargetSystem, BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter>>
            buildSpecialized() {
        Map<TargetSystem, BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter>> map =
                new EnumMap<>(TargetSystem.class);
        map.put(TargetSystem.OPENMRS, OpenMrsResourceAdapter::new);
        map.put(TargetSystem.OPENEMR, OpenEmrResourceAdapter::new);
        map.put(TargetSystem.HAPI_FHIR, HapiFhirResourceAdapter::new);
        map.put(TargetSystem.LOCAL_OMIICARE, OmiiCareResourceAdapter::new);
        return map;
    }

    /**
     * Returns the specialized supplier registered for {@code system}, if the framework provides one.
     * Systems without a dedicated adapter return {@link Optional#empty()} and resolve through the
     * generic HTTP adapter.
     */
    public static Optional<BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter>> supplierFor(
            TargetSystem system) {
        return Optional.ofNullable(SPECIALIZED.get(system));
    }

    /** True when a specialized (non-generic) adapter is registered for the given system. */
    public static boolean hasSpecializedAdapter(TargetSystem system) {
        return SPECIALIZED.containsKey(system);
    }

    /**
     * Resolves an adapter for {@code system} using the default {@link FrameworkConfig}. Specialized
     * adapters are preferred; otherwise the generic {@link HttpResourceAdapter} is used.
     */
    public static ResourceAdapter resolve(TargetSystem system) {
        return resolve(system, FrameworkConfig.get());
    }

    /**
     * Resolves an adapter for {@code system} with the supplied configuration. Specialized adapters
     * are preferred; otherwise the generic {@link HttpResourceAdapter} is used. The returned adapter
     * always reports the requested {@code system} via {@link ResourceAdapter#system()}.
     */
    public static ResourceAdapter resolve(TargetSystem system, FrameworkConfig config) {
        BiFunction<TargetSystem, FrameworkConfig, ResourceAdapter> supplier =
                SPECIALIZED.getOrDefault(system, HttpResourceAdapter::new);
        return supplier.apply(system, config);
    }

    /**
     * Registers every framework-known specialized supplier with the shared {@link AdapterFactory},
     * so callers that go through the factory transparently receive the richer adapters. Idempotent.
     */
    public static void installInto() {
        SPECIALIZED.forEach(AdapterFactory::register);
    }
}
