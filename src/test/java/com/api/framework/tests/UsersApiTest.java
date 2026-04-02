package com.api.framework.tests;

import com.api.framework.constants.ApiConstants;
import com.api.framework.models.Post;
import com.api.framework.models.User;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Test suite for the /users endpoint of JSONPlaceholder API.
 *
 * Tests covered:
 *  1. GET /users          – returns a list with valid email addresses
 *  2. GET /users/{id}     – returns correct user details
 *  3. GET /users/{id}/posts – returns all posts belonging to the user
 */
@Epic("Users API")
@Feature("User Resource Operations")
public class UsersApiTest extends BaseTest {

    // ─── Test 1: GET /users ───────────────────────────────────────────────────

    @Test(description = "GET /users should return a non-empty list where every user has a valid email")
    @Story("Get All Users")
    @Severity(SeverityLevel.BLOCKER)
    public void getAllUsers_shouldReturnListWithValidEmails() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(ApiConstants.Users.BASE)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        List<User> users = JsonUtils.listFromResponse(response, User.class);
        Assert.assertFalse(users.isEmpty(), "User list should not be empty");

        for (User user : users) {
            Assert.assertNotNull(user.getId(),    "User id should not be null");
            Assert.assertNotNull(user.getName(),  "User name should not be null");
            // Basic email format check
            String email = user.getEmail();
            Assert.assertNotNull(email, "Email should not be null");
            Assert.assertTrue(email.contains("@") && email.contains("."),
                    "Email '" + email + "' does not look valid for user id=" + user.getId());
        }

        log.info("Validated {} users — all have well-formed emails", users.size());
    }

    // ─── Test 2: GET /users/{id} ──────────────────────────────────────────────

    @Test(description = "GET /users/{id} should return the correct user for a known ID")
    @Story("Get User By ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserById_shouldReturnExpectedUser() {

        int userId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", userId)
            .when()
                .get(ApiConstants.Users.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        User user = JsonUtils.fromResponse(response, User.class);

        Assert.assertEquals(user.getId(), userId, "User id in response should match requested id");
        Assert.assertFalse(user.getName().isBlank(),     "Name should not be blank");
        Assert.assertFalse(user.getUsername().isBlank(), "Username should not be blank");
        Assert.assertTrue(user.getEmail().contains("@"),  "Email should be valid");

        log.info("Fetched user: id={}, name='{}', email='{}'",
                user.getId(), user.getName(), user.getEmail());
    }

    // ─── Test 3: GET /users/{id}/posts ────────────────────────────────────────

    @Test(description = "GET /users/{id}/posts should return all posts belonging to that user")
    @Story("Get Posts By User")
    @Severity(SeverityLevel.NORMAL)
    public void getUserPosts_shouldReturnPostsOwnedByUser() {

        int userId = 1;

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", userId)
            .when()
                .get(ApiConstants.Users.POSTS)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        List<Post> posts = JsonUtils.listFromResponse(response, Post.class);
        Assert.assertFalse(posts.isEmpty(), "User " + userId + " should have at least one post");

        // Verify every returned post belongs to this user
        for (Post post : posts) {
            Assert.assertEquals(post.getUserId(), userId,
                    "Post id=" + post.getId() + " has userId=" + post.getUserId()
                    + " but expected userId=" + userId);
        }

        log.info("User id={} owns {} posts — all correctly attributed", userId, posts.size());
    }
}
