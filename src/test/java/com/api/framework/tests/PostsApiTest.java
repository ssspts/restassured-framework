package com.api.framework.tests;

import com.api.framework.constants.ApiConstants;
import com.api.framework.models.Post;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.TestDataBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Test suite for the /posts endpoint of JSONPlaceholder API.
 *
 * Tests covered:
 *  1. GET all posts            – returns a non-empty array with correct structure
 *  2. GET single post by ID    – returns the correct post fields
 *  3. POST create a new post   – verifies resource is created and echoed back
 *  4. PUT update a post        – verifies full update is reflected in response
 */
@Epic("Posts API")
@Feature("CRUD Operations on /posts")
public class PostsApiTest extends BaseTest {

    // ─── Test 1: GET /posts ───────────────────────────────────────────────────

    @Test(description = "GET /posts should return a non-empty list of posts")
    @Story("Get All Posts")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that GET /posts returns HTTP 200, a non-empty JSON array, " +
                 "and each element contains 'id', 'userId', 'title', and 'body'.")
    public void getAllPosts_shouldReturn200WithNonEmptyList() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(ApiConstants.Posts.BASE)
            .then()
                .extract().response();

        // Status & content-type
        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        ResponseValidator.assertContentTypeJson(response);

        // Deserialise and validate structure
        List<Post> posts = JsonUtils.listFromResponse(response, Post.class);

        Assert.assertFalse(posts.isEmpty(), "Post list should not be empty");
        log.info("Received {} posts", posts.size());

        // Spot-check first element
        Post first = posts.get(0);
        Assert.assertNotNull(first.getId(),     "Post id should not be null");
        Assert.assertNotNull(first.getUserId(), "Post userId should not be null");
        Assert.assertNotNull(first.getTitle(),  "Post title should not be null");
        Assert.assertNotNull(first.getBody(),   "Post body should not be null");

        // Performance gate
        ResponseValidator.assertResponseTime(response, 5000);
    }

    // ─── Test 2: GET /posts/{id} ──────────────────────────────────────────────

    @Test(description = "GET /posts/{id} should return the correct post for a valid ID")
    @Story("Get Post By ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that GET /posts/1 returns HTTP 200 and the response body " +
                 "contains the correct id, userId, non-blank title, and body.")
    public void getPostById_shouldReturnCorrectPost() {

        int postId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
            .when()
                .get(ApiConstants.Posts.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        ResponseValidator.assertContentTypeJson(response);

        Post post = JsonUtils.fromResponse(response, Post.class);

        Assert.assertEquals(post.getId(), postId,
                "Returned post id does not match the requested id");
        Assert.assertNotNull(post.getUserId(), "userId should not be null");
        Assert.assertFalse(post.getTitle().isBlank(), "Title should not be blank");
        Assert.assertFalse(post.getBody().isBlank(),  "Body should not be blank");

        log.info("Fetched post: id={}, title='{}'", post.getId(), post.getTitle());
    }

    // ─── Test 3: POST /posts ──────────────────────────────────────────────────

    @Test(description = "POST /posts should create a new post and return 201 with the created resource")
    @Story("Create Post")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that a POST to /posts with a valid body returns HTTP 201, " +
                 "and the response echoes back the submitted title, body, and userId along with a new id.")
    public void createPost_shouldReturn201WithNewId() {

        Post newPost = TestDataBuilder.buildPost(1);
        log.info("Creating post: title='{}'", newPost.getTitle());

        Response response = given()
                .spec(requestSpec)
                .body(JsonUtils.toJson(newPost))
            .when()
                .post(ApiConstants.Posts.BASE)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_CREATED);
        ResponseValidator.assertContentTypeJson(response);

        Post created = JsonUtils.fromResponse(response, Post.class);

        // JSONPlaceholder echoes back the fields and assigns id=101
        Assert.assertNotNull(created.getId(),    "Created post should have an id");
        Assert.assertEquals(created.getUserId(), newPost.getUserId(),
                "userId in response should match the request");
        Assert.assertEquals(created.getTitle(),  newPost.getTitle(),
                "Title in response should match the request");
        Assert.assertEquals(created.getBody(),   newPost.getBody(),
                "Body in response should match the request");

        log.info("Post created with id={}", created.getId());
    }

    // ─── Test 4: PUT /posts/{id} ──────────────────────────────────────────────

    @Test(description = "PUT /posts/{id} should fully update a post and return 200 with updated fields")
    @Story("Update Post")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that a PUT to /posts/1 with a complete replacement body returns HTTP 200 " +
                 "and the response reflects all updated fields correctly.")
    public void updatePost_shouldReturn200WithUpdatedFields() {

        int postId = 1;
        Post updatedPost = Post.builder()
                .id(postId)
                .userId(1)
                .title("Updated Title — " + System.currentTimeMillis())
                .body("This is the fully updated body content for the post.")
                .build();

        log.info("Updating post id={} with title='{}'", postId, updatedPost.getTitle());

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(JsonUtils.toJson(updatedPost))
            .when()
                .put(ApiConstants.Posts.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        ResponseValidator.assertContentTypeJson(response);

        Post result = JsonUtils.fromResponse(response, Post.class);

        Assert.assertEquals(result.getId(),     postId,               "Post id should remain unchanged");
        Assert.assertEquals(result.getTitle(),  updatedPost.getTitle(), "Updated title should be reflected");
        Assert.assertEquals(result.getBody(),   updatedPost.getBody(),  "Updated body should be reflected");
        Assert.assertEquals(result.getUserId(), updatedPost.getUserId(),"UserId should be preserved");

        log.info("Post id={} updated successfully", result.getId());
    }
}
