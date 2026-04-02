package com.api.framework.utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;

/**
 * Reusable assertion helpers for REST Assured responses.
 * Wraps common checks with descriptive failure messages.
 */
public class ResponseValidator {

    private static final Logger log = LoggerFactory.getLogger(ResponseValidator.class);

    private ResponseValidator() {}

    // ─── Status Code ──────────────────────────────────────────────────────────

    public static void assertStatusCode(Response response, int expectedStatus) {
        int actual = response.getStatusCode();
        log.info("Validating status code: expected={}, actual={}", expectedStatus, actual);
        Assert.assertEquals(actual, expectedStatus,
                "Expected HTTP status " + expectedStatus + " but got " + actual
                        + ". Response body: " + response.asString());
    }

    // ─── Body Field Assertions ────────────────────────────────────────────────

    public static void assertFieldNotNull(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        Assert.assertNotNull(value,
                "Expected field '" + jsonPath + "' to be present and non-null. Response: " + response.asString());
    }

    public static void assertFieldEquals(Response response, String jsonPath, Object expected) {
        Object actual = response.jsonPath().get(jsonPath);
        Assert.assertEquals(actual, expected,
                "Field '" + jsonPath + "' mismatch. Expected: " + expected + ", Actual: " + actual);
    }

    public static void assertFieldNotEmpty(Response response, String jsonPath) {
        String value = response.jsonPath().getString(jsonPath);
        Assert.assertTrue(value != null && !value.isBlank(),
                "Expected field '" + jsonPath + "' to be non-empty. Got: " + value);
    }

    // ─── List / Array Assertions ──────────────────────────────────────────────

    public static void assertListNotEmpty(Response response, String jsonPath) {
        List<?> list = response.jsonPath().getList(jsonPath);
        Assert.assertNotNull(list, "Expected list at '" + jsonPath + "' but got null");
        Assert.assertFalse(list.isEmpty(), "Expected list at '" + jsonPath + "' to be non-empty");
    }

    public static void assertListSize(Response response, String jsonPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(jsonPath);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), expectedSize,
                "Expected list size " + expectedSize + " at '" + jsonPath + "' but got " + list.size());
    }

    public static void assertListSizeGreaterThan(Response response, String jsonPath, int minSize) {
        List<?> list = response.jsonPath().getList(jsonPath);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > minSize,
                "Expected list at '" + jsonPath + "' to have more than " + minSize + " items, but got " + list.size());
    }

    // ─── Header Assertions ────────────────────────────────────────────────────

    public static void assertContentTypeJson(Response response) {
        String contentType = response.getContentType();
        Assert.assertTrue(contentType != null && contentType.contains("application/json"),
                "Expected Content-Type to contain 'application/json', but got: " + contentType);
    }

    public static void assertResponseTime(Response response, long maxMillis) {
        long actual = response.getTime();
        log.info("Response time: {}ms (limit: {}ms)", actual, maxMillis);
        Assert.assertTrue(actual <= maxMillis,
                "Response time " + actual + "ms exceeded limit of " + maxMillis + "ms");
    }
}
