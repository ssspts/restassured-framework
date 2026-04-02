package com.api.framework.tests;

import com.api.framework.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all test classes.
 * Provides the shared RequestSpecification and common lifecycle hooks.
 */
public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    /** Shared request spec — initialised once per test class. */
    protected RequestSpecification requestSpec;

    @BeforeClass(alwaysRun = true)
    public void setUpSpec() {
        requestSpec = RestAssuredConfig.getBaseSpec();
        log.info("▌ Test class initialised: {}", this.getClass().getSimpleName());
    }
}
