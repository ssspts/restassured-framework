package com.api.framework.tests;

import com.api.framework.constants.ApiConstants;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.SchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Demonstrates JSON Schema validation using SchemaValidator + classpath schema files.
 *
 * Tests covered:
 *  1. GET /posts/{id}  — validates response against post-schema.json
 *  2. GET /users/{id}  — validates response against user-schema.json
 */
@Epic("Schema Validation")
@Feature("JSON Schema Contract Tests")
public class SchemaValidationTest extends BaseTest {

    @Test(description = "GET /posts/{id} response should conform to post-schema.json",
          groups = {"regression", "contract"})
    @Story("Post Schema Contract")
    @Severity(SeverityLevel.CRITICAL)
    public void getPost_responseShouldMatchJsonSchema() {

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", 1)
            .when()
                .get(ApiConstants.Posts.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        SchemaValidator.validate(response, "post-schema.json");

        log.info("Post schema validation passed for id=1");
    }

    @Test(description = "GET /users/{id} response should conform to user-schema.json",
          groups = {"regression", "contract"})
    @Story("User Schema Contract")
    @Severity(SeverityLevel.CRITICAL)
    public void getUser_responseShouldMatchJsonSchema() {

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", 1)
            .when()
                .get(ApiConstants.Users.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        SchemaValidator.validate(response, "user-schema.json");

        log.info("User schema validation passed for id=1");
    }
}
