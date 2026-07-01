package com.omiicare.qa.automation.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit 5 {@link TestWatcher} extension that mirrors each test outcome into the Extent report.
 *
 * <p>Register on a test class with {@code @ExtendWith(ExtentJUnitListener.class)}. For every test
 * method the watcher creates an {@link ExtentTest} node named after the display name and records the
 * terminal status (pass / fail / abort / disabled). On completion the aggregate report is flushed so
 * results survive even an abrupt JVM exit between classes.
 *
 * <p>The extension keeps no mutable instance state beyond the per-invocation node, so a single
 * instance is safe across parallel test methods (JUnit creates the node fresh in each callback).
 */
public class ExtentJUnitListener implements TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ExtentJUnitListener.class);

    private ExtentTest nodeFor(ExtensionContext context) {
        ExtentReportManager manager = ExtentReportManager.getInstance();
        String name = context.getDisplayName();
        String description =
                context.getTestMethod().map(m -> m.getDeclaringClass().getSimpleName() + "#" + m.getName())
                        .orElse(name);
        ExtentTest test = manager.createTest(name, description);
        context.getTags().forEach(test::assignCategory);
        return test;
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        nodeFor(context).log(Status.PASS, "Test passed");
        ExtentReportManager.getInstance().flush();
        LOG.debug("PASS  {}", context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ExtentTest test = nodeFor(context);
        test.log(Status.FAIL, "Test failed: " + describe(cause));
        if (cause != null) {
            test.fail(cause);
        }
        ExtentReportManager.getInstance().flush();
        LOG.debug("FAIL  {}", context.getDisplayName());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        nodeFor(context).log(Status.WARNING, "Test aborted: " + describe(cause));
        ExtentReportManager.getInstance().flush();
        LOG.debug("ABORT {}", context.getDisplayName());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        nodeFor(context).log(Status.SKIP, "Test disabled: " + reason.orElse("no reason given"));
        ExtentReportManager.getInstance().flush();
        LOG.debug("SKIP  {}", context.getDisplayName());
    }

    private static String describe(Throwable cause) {
        if (cause == null) {
            return "unknown";
        }
        String message = cause.getMessage();
        return message == null ? cause.getClass().getSimpleName() : message;
    }
}
