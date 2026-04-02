package com.api.framework.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class that creates reusable RestAssured RequestSpecifications.
 * Centralises base URL, headers, timeouts and logging configuration.
 */
public class RestAssuredConfig {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredConfig.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    private RestAssuredConfig() {}

    /**
     * Returns a base RequestSpecification with common defaults applied.
     */
    public static RequestSpecification getBaseSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())            // attach req/res to Allure report
                .setRelaxedHTTPSValidation();                  // trust all SSL certs (test envs)

        if (config.isLoggingEnabled()) {
            builder.log(LogDetail.ALL);
        }

        String token = config.getAuthToken();
        if (token != null && !token.isBlank()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        log.info("RestAssured base spec created for: {}", config.getBaseUrl());
        return builder.build();
    }

    /**
     * Returns a spec pre-loaded with a given bearer token.
     */
    public static RequestSpecification getSpecWithToken(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(getBaseSpec())
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * Returns a spec without the Content-Type header (useful for multipart/form-data).
     */
    public static RequestSpecification getMultipartSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(getBaseSpec())
                .setContentType(ContentType.MULTIPART)
                .build();
    }
}
