package com.api.framework.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple TestNG reporter that writes a plain-text summary to stdout.
 * Serves as a lightweight alternative/complement to Allure for local runs.
 *
 * For a full HTML report, swap this class body with the ExtentReports
 * library (com.aventstack:extentreports) following the same hook points.
 */
public class ExtentReportManager implements ISuiteListener, ITestListener {

    private static final Logger log = LoggerFactory.getLogger(ExtentReportManager.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int passed;
    private int failed;
    private int skipped;

    // ─── Suite lifecycle ──────────────────────────────────────────────────────

    @Override
    public void onStart(ISuite suite) {
        passed = failed = skipped = 0;
        log.info("╔══════════════════════════════════════════════════════╗");
        log.info("║  Suite started : {}",  suite.getName());
        log.info("║  Started at    : {}", LocalDateTime.now().format(FMT));
        log.info("╚══════════════════════════════════════════════════════╝");
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("╔══════════════════════════════════════════════════════╗");
        log.info("║  Suite finished : {}", suite.getName());
        log.info("║  Finished at    : {}", LocalDateTime.now().format(FMT));
        log.info("║  ✔ Passed  : {}", passed);
        log.info("║  ✘ Failed  : {}", failed);
        log.info("║  ⊘ Skipped : {}", skipped);
        log.info("╚══════════════════════════════════════════════════════╝");
    }

    // ─── Test lifecycle ───────────────────────────────────────────────────────

    @Override
    public void onTestSuccess(ITestResult result) { passed++; }

    @Override
    public void onTestFailure(ITestResult result) { failed++; }

    @Override
    public void onTestSkipped(ITestResult result) { skipped++; }
}
