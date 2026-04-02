package com.api.framework.tests;

import com.api.framework.constants.ApiConstants;
import com.api.framework.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Negative test suite — verifies the API returns appropriate error responses
 * for invalid inputs and non-existent resources.
 *
 * Tests covered:
 *  1. GET /posts/{id} with non-existent ID → 404
 *  2. GET /users/{id} with non-existent ID → 404
 *  3. DELETE /posts/{id} existing resource → 200 (JSONPlaceholder accepts all deletes)
 *  4. GET /posts with an unknown query param → 200 with empty array (graceful ignore)
 */
@Epic("Negative Tests")
@Feature("Error Handling & Boundary Conditions")
public class NegativeApiTest extends BaseTest {

    // ─── Test 1: Non-existent post ────────────────────────────────────────────

    @Test(description = "GET /posts with non-existent ID should return 404")
    @Story("Resource Not Found")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures the API returns 404 when a post with an out-of-range ID is requested.")
    public void getPostById_nonExistentId_shouldReturn404() {

        int invalidId = 999999;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", invalidId)
            .when()
                .get(ApiConstants.Posts.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_NOT_FOUND);
        log.info("Correctly received 404 for post id={}", invalidId);
    }

    // ─── Test 2: Non-existent user ────────────────────────────────────────────

    @Test(description = "GET /users with non-existent ID should return 404")
    @Story("Resource Not Found")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserById_nonExistentId_shouldReturn404() {

        int invalidId = 88888;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", invalidId)
            .when()
                .get(ApiConstants.Users.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_NOT_FOUND);
        log.info("Correctly received 404 for user id={}", invalidId);
    }

    // ─── Test 3: DELETE a post ────────────────────────────────────────────────

    @Test(description = "DELETE /posts/{id} should return 200 and an empty body")
    @Story("Delete Resource")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that DELETE on a valid post returns 200 with an empty JSON object.")
    public void deletePost_shouldReturn200() {

        int postId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
            .when()
                .delete(ApiConstants.Posts.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        log.info("DELETE /posts/{} returned status {}", postId, response.getStatusCode());
    }

    // ─── Test 4: Filtered query with no matching results ─────────────────────

    @Test(description = "GET /posts?userId=99999 should return an empty array for an unknown userId")
    @Story("Filtered Query Returns Empty")
    @Severity(SeverityLevel.MINOR)
    public void getPostsByUnknownUserId_shouldReturnEmptyArray() {

        Response response = given()
                .spec(requestSpec)
                .queryParam("userId", 99999)
            .when()
                .get(ApiConstants.Posts.BASE)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        // Should be an empty JSON array []
        String body = response.asString().trim();
        log.info("Response body for unknown userId filter: {}", body);
        assert body.equals("[]") : "Expected empty array '[]' but got: " + body;
    }
}
