package com.api.framework.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer — automatically retries a failing test up to MAX_RETRY_COUNT times.
 *
 * Usage: annotate any test method with:
 *   @Test(retryAnalyzer = RetryAnalyzer.class)
 *
 * Or apply globally via a TestNG listener that sets the analyzer on every method.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT = 2;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("⟳ Retrying test '{}' — attempt {}/{}",
                    result.getName(), retryCount, MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }
}
