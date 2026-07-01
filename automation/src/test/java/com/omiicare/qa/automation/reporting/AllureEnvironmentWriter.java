package com.omiicare.qa.automation.reporting;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes Allure's {@code environment.properties} file into the Allure results directory.
 *
 * <p>Allure renders the key/value pairs from this file in the "Environment" widget of the generated
 * report. This writer captures the active framework environment, JVM, OS, and the configured target
 * base URIs so every report is self-describing. The results directory defaults to {@code
 * allure-results} (the Allure convention) and can be overridden via {@code omii.report.allure.dir}.
 *
 * <p>Usage: call {@link #write()} once during suite startup (for example from a JUnit {@code
 * @BeforeAll} hook or a TestNG suite listener) before tests begin emitting results.
 */
public final class AllureEnvironmentWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AllureEnvironmentWriter.class);

    /** Configuration key for the Allure results directory. */
    public static final String RESULTS_DIR_KEY = "omii.report.allure.dir";

    /** Default Allure results directory, relative to the working directory. */
    public static final String DEFAULT_RESULTS_DIR = "allure-results";

    private AllureEnvironmentWriter() {
        // static utility
    }

    /**
     * Writes {@code environment.properties} using the default environment metadata.
     *
     * @return the file written
     */
    public static File write() {
        return write(defaultEnvironment());
    }

    /**
     * Writes {@code environment.properties} using the supplied key/value pairs.
     *
     * @param values ordered environment metadata; iteration order is preserved in the output
     * @return the file written
     */
    public static File write(Map<String, String> values) {
        FrameworkConfig config = FrameworkConfig.get();
        File dir = new File(config.get(RESULTS_DIR_KEY, DEFAULT_RESULTS_DIR));
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.warn("Could not create Allure results directory: {}", dir.getAbsolutePath());
        }
        File target = new File(dir, "environment.properties");

        // Properties does not preserve order, so emit deterministically ourselves while still
        // routing through Properties.store to get correct escaping of special characters.
        Properties props = new OrderedProperties(values.keySet());
        values.forEach((k, v) -> props.setProperty(k, v == null ? "" : v));

        try {
            Files.createDirectories(dir.toPath());
            try (OutputStream out = Files.newOutputStream(target.toPath())) {
                props.store(out, "OmiiCare QA Automation - Allure environment");
            }
            LOG.info("Allure environment written -> {}", target.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Failed to write Allure environment.properties", e);
            throw new IllegalStateException("Failed to write " + target.getAbsolutePath(), e);
        }
        return target;
    }

    /** Builds the default metadata map from configuration and JVM/OS properties. */
    public static Map<String, String> defaultEnvironment() {
        FrameworkConfig config = FrameworkConfig.get();
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Environment", config.environment());
        values.put("Java.Version", System.getProperty("java.version", "unknown"));
        values.put("Java.Vendor", System.getProperty("java.vendor", "unknown"));
        values.put("OS.Name", System.getProperty("os.name", "unknown"));
        values.put("OS.Arch", System.getProperty("os.arch", "unknown"));
        values.put("User.Timezone", System.getProperty("user.timezone", "unknown"));
        return values;
    }

    /**
     * A {@link Properties} subclass that returns keys in a stable, caller-defined order so the
     * generated file is reproducible across runs.
     */
    private static final class OrderedProperties extends Properties {
        private final java.util.List<Object> order;

        OrderedProperties(java.util.Collection<String> keys) {
            this.order = new java.util.ArrayList<>(keys);
        }

        @Override
        public synchronized Object put(Object key, Object value) {
            if (!order.contains(key)) {
                order.add(key);
            }
            return super.put(key, value);
        }

        @Override
        public java.util.Set<Object> keySet() {
            return new java.util.LinkedHashSet<>(order);
        }

        @Override
        public synchronized java.util.Enumeration<Object> keys() {
            return java.util.Collections.enumeration(order);
        }
    }
}
