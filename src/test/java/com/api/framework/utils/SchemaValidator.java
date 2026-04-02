package com.api.framework.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility that validates a RestAssured response body against a JSON Schema file
 * stored under src/test/resources/schemas/.
 *
 * Dependencies already included via rest-assured — no extra dep needed.
 *
 * Usage:
 *   SchemaValidator.validate(response, "post-schema.json");
 */
public class SchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(SchemaValidator.class);
    private static final String SCHEMA_DIR = "schemas/";

    private SchemaValidator() {}

    /**
     * Validates the response body against a JSON Schema file from the classpath.
     *
     * @param response   the RestAssured response to validate
     * @param schemaFile file name relative to src/test/resources/schemas/ (e.g. "post-schema.json")
     */
    public static void validate(Response response, String schemaFile) {
        String schemaPath = SCHEMA_DIR + schemaFile;
        log.info("Validating response against schema: {}", schemaPath);

        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));

        log.info("Schema validation passed for: {}", schemaFile);
    }
}
