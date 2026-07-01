package com.omiicare.qa.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe singleton wrapper around {@link ExtentReports}.
 *
 * <p>Owns a single {@link ExtentReports} aggregate backed by an {@link ExtentSparkReporter} that
 * writes an HTML report to {@code target/extent} (overridable via the {@code omii.report.extent.dir}
 * configuration key). Individual tests obtain per-test nodes through {@link #createTest(String,
 * String)}; node references can be parked on the calling thread with {@link #setCurrentTest} so that
 * listeners running on the same thread can attach status and logs without passing the node around.
 *
 * <p>The instance is created lazily on first access. {@link #flush()} persists the accumulated
 * report to disk and is safe to call repeatedly.
 */
public final class ExtentReportManager {

    private static final Logger LOG = LoggerFactory.getLogger(ExtentReportManager.class);

    /** Configuration key for the output directory of the Extent HTML report. */
    public static final String REPORT_DIR_KEY = "omii.report.extent.dir";

    /** Default output directory, relative to the working directory. */
    public static final String DEFAULT_REPORT_DIR = "target/extent";

    private static volatile ExtentReportManager instance;

    private final ExtentReports extent;
    private final File reportFile;
    private final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    private ExtentReportManager() {
        String dir =
                FrameworkConfig.get().get(REPORT_DIR_KEY, DEFAULT_REPORT_DIR);
        File outputDir = new File(dir);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            LOG.warn("Could not create Extent report directory: {}", outputDir.getAbsolutePath());
        }
        String stamp =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        this.reportFile = new File(outputDir, "extent-report-" + stamp + ".html");

        ExtentSparkReporter spark = new ExtentSparkReporter(this.reportFile);
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("OmiiCare QA Automation Report");
        spark.config().setReportName("OmiiCare Healthcare Test Automation");
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        this.extent = new ExtentReports();
        this.extent.attachReporter(spark);
        this.extent.setSystemInfo("Environment", FrameworkConfig.get().environment());
        this.extent.setSystemInfo("Java", System.getProperty("java.version", "unknown"));
        this.extent.setSystemInfo("OS", System.getProperty("os.name", "unknown"));

        LOG.info("ExtentReportManager initialized -> {}", this.reportFile.getAbsolutePath());
    }

    /** Returns the lazily-created singleton, building it on first call. */
    public static ExtentReportManager getInstance() {
        ExtentReportManager local = instance;
        if (local == null) {
            synchronized (ExtentReportManager.class) {
                local = instance;
                if (local == null) {
                    local = new ExtentReportManager();
                    instance = local;
                }
            }
        }
        return local;
    }

    /** Underlying aggregate report (rarely needed directly). */
    public ExtentReports getExtent() {
        return extent;
    }

    /** Creates a new top-level test node with the given name and optional description. */
    public ExtentTest createTest(String name, String description) {
        ExtentTest test =
                (description == null || description.isBlank())
                        ? extent.createTest(name)
                        : extent.createTest(name, description);
        currentTest.set(test);
        return test;
    }

    /** Parks the supplied node on the current thread for listener access. */
    public void setCurrentTest(ExtentTest test) {
        currentTest.set(test);
    }

    /** Returns the node parked on the current thread, or {@code null} if none. */
    public ExtentTest getCurrentTest() {
        return currentTest.get();
    }

    /** Clears the thread-local node reference. */
    public void clearCurrentTest() {
        currentTest.remove();
    }

    /** The HTML file this manager writes to. */
    public File getReportFile() {
        return reportFile;
    }

    /** Persists all buffered results to disk; safe to call multiple times. */
    public void flush() {
        extent.flush();
        LOG.info("Extent report flushed -> {}", reportFile.getAbsolutePath());
    }
}
