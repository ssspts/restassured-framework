package com.api.framework.listeners;

import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that hooks into the test lifecycle to:
 * - Log test start/pass/fail/skip to console
 * - Attach failure details to Allure reports
 */
public class AllureTestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(AllureTestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ STARTING: {}.{}", result.getTestClass().getName(), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔ PASSED:   {}.{} ({}ms)",
                result.getTestClass().getName(),
                result.getName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✘ FAILED:   {}.{}", result.getTestClass().getName(), result.getName());
        if (result.getThrowable() != null) {
            log.error("  Cause: {}", result.getThrowable().getMessage());
            attachFailureDetails(result.getThrowable().toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⊘ SKIPPED:  {}.{}", result.getTestClass().getName(), result.getName());
    }

    @Attachment(value = "Failure Details", type = "text/plain")
    private String attachFailureDetails(String details) {
        return details;
    }
}
