package com.api.framework.tests;

import com.api.framework.constants.ApiConstants;
import com.api.framework.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Test suite for the /comments endpoint.
 *
 * Tests covered:
 *  1. GET /posts/{id}/comments  – comments returned belong to the correct post
 *  2. GET /comments?postId=1    – query-param filter matches path-param result
 *  3. GET /comments/{id}        – single comment has a valid email field
 */
@Epic("Comments API")
@Feature("Comment Resource Operations")
public class CommentsApiTest extends BaseTest {

    // ─── Test 1: GET /posts/{postId}/comments ─────────────────────────────────

    @Test(description = "GET /posts/{id}/comments should return comments linked to that post",
          groups = {"smoke", "regression"})
    @Story("Get Comments For Post")
    @Severity(SeverityLevel.CRITICAL)
    public void getCommentsByPostId_pathParam_shouldReturnRelatedComments() {

        int postId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
            .when()
                .get(ApiConstants.Posts.COMMENTS)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        ResponseValidator.assertContentTypeJson(response);

        List<Map<String, Object>> comments = response.jsonPath().getList("$");
        Assert.assertFalse(comments.isEmpty(),
                "Expected at least one comment for postId=" + postId);

        // Every comment must link back to this post
        comments.forEach(comment -> {
            Object linkedPostId = comment.get("postId");
            Assert.assertNotNull(linkedPostId, "Comment postId should not be null");
            Assert.assertEquals(((Number) linkedPostId).intValue(), postId,
                    "Comment postId " + linkedPostId + " should equal requested postId=" + postId);
        });

        log.info("PostId={} has {} comments — all linked correctly", postId, comments.size());
    }

    // ─── Test 2: Query-param vs path-param equivalence ────────────────────────

    @Test(description = "GET /comments?postId=1 and GET /posts/1/comments should return identical results",
          groups = {"regression"})
    @Story("Comment Filter Equivalence")
    @Severity(SeverityLevel.NORMAL)
    public void getComments_queryParamFilter_shouldMatchPathParamResult() {

        int postId = 1;

        // Approach A — query parameter
        Response queryResponse = given()
                .spec(requestSpec)
                .queryParam("postId", postId)
            .when()
                .get(ApiConstants.Comments.BASE)
            .then()
                .extract().response();

        // Approach B — nested path
        Response pathResponse = given()
                .spec(requestSpec)
                .pathParam("id", postId)
            .when()
                .get(ApiConstants.Posts.COMMENTS)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(queryResponse, ApiConstants.STATUS_OK);
        ResponseValidator.assertStatusCode(pathResponse,  ApiConstants.STATUS_OK);

        List<?> queryList = queryResponse.jsonPath().getList("$");
        List<?> pathList  = pathResponse.jsonPath().getList("$");

        Assert.assertEquals(queryList.size(), pathList.size(),
                "Both approaches should return the same number of comments");

        log.info("Both endpoints returned {} comments for postId={}", queryList.size(), postId);
    }

    // ─── Test 3: GET /comments/{id} — email field validation ─────────────────

    @Test(description = "GET /comments/{id} should return a comment with a valid email address",
          groups = {"regression"})
    @Story("Get Comment By ID")
    @Severity(SeverityLevel.NORMAL)
    public void getCommentById_shouldContainValidEmail() {

        int commentId = 3;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", commentId)
            .when()
                .get(ApiConstants.Comments.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        String name    = response.jsonPath().getString("name");
        String email   = response.jsonPath().getString("email");
        String body    = response.jsonPath().getString("body");
        int    id      = response.jsonPath().getInt("id");

        Assert.assertEquals(id, commentId, "Returned comment id should match requested id");
        Assert.assertFalse(name.isBlank(),  "Comment name should not be blank");
        Assert.assertFalse(body.isBlank(),  "Comment body should not be blank");

        // Email format check
        Assert.assertNotNull(email, "Email should be present");
        Assert.assertTrue(email.contains("@") && email.contains("."),
                "Email '" + email + "' does not appear to be valid");

        log.info("Comment id={} has email='{}'", id, email);
    }
}
