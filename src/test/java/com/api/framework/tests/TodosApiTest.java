package com.api.framework.tests;

import com.api.framework.constants.ApiConstants;
import com.api.framework.models.Todo;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.TestDataBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Test suite for the /todos endpoint.
 *
 * Tests covered:
 *  1. GET /todos          – non-empty list, all items have required fields
 *  2. GET /todos?completed=true – filter returns only completed todos
 *  3. POST /todos         – create and verify echo
 *  4. PATCH /todos/{id}   – partial update of 'completed' flag
 *  5. DataProvider-driven – GET /todos/{id} for multiple IDs
 */
@Epic("Todos API")
@Feature("Todo Resource Operations")
public class TodosApiTest extends BaseTest {

    // ─── Test 1: GET /todos ───────────────────────────────────────────────────

    @Test(description = "GET /todos should return a non-empty list with valid structure",
          groups = {"smoke", "regression"})
    @Story("Get All Todos")
    @Severity(SeverityLevel.BLOCKER)
    public void getAllTodos_shouldReturnNonEmptyList() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(ApiConstants.Todos.BASE)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);
        ResponseValidator.assertContentTypeJson(response);

        List<Todo> todos = JsonUtils.listFromResponse(response, Todo.class);
        Assert.assertFalse(todos.isEmpty(), "Todos list should not be empty");

        // Validate every item has required fields
        todos.forEach(todo -> {
            Assert.assertNotNull(todo.getId(),        "Todo id must not be null");
            Assert.assertNotNull(todo.getUserId(),    "Todo userId must not be null");
            Assert.assertNotNull(todo.getTitle(),     "Todo title must not be null");
            Assert.assertNotNull(todo.getCompleted(), "Todo completed flag must not be null");
        });

        log.info("Validated {} todos — all fields present", todos.size());
    }

    // ─── Test 2: GET /todos?completed=true ────────────────────────────────────

    @Test(description = "GET /todos?completed=true should return only completed todos",
          groups = {"regression"})
    @Story("Filter Todos by Completion Status")
    @Severity(SeverityLevel.NORMAL)
    public void getTodos_filteredByCompleted_shouldReturnOnlyCompletedItems() {

        Response response = given()
                .spec(requestSpec)
                .queryParam("completed", true)
            .when()
                .get(ApiConstants.Todos.BASE)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        List<Todo> todos = JsonUtils.listFromResponse(response, Todo.class);
        Assert.assertFalse(todos.isEmpty(), "Filtered todos list should not be empty");

        long nonCompleted = todos.stream()
                .filter(t -> !Boolean.TRUE.equals(t.getCompleted()))
                .count();

        Assert.assertEquals(nonCompleted, 0L,
                "Expected all returned todos to have completed=true, but found "
                + nonCompleted + " incomplete ones");

        log.info("Filter returned {} completed todos — all validated", todos.size());
    }

    // ─── Test 3: POST /todos ──────────────────────────────────────────────────

    @Test(description = "POST /todos should create a new todo and echo it back with a new id",
          groups = {"regression"})
    @Story("Create Todo")
    @Severity(SeverityLevel.CRITICAL)
    public void createTodo_shouldReturn201WithNewId() {

        Todo newTodo = TestDataBuilder.buildCompletedTodo(1);
        log.info("Creating todo: title='{}'", newTodo.getTitle());

        Response response = given()
                .spec(requestSpec)
                .body(JsonUtils.toJson(newTodo))
            .when()
                .post(ApiConstants.Todos.BASE)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_CREATED);

        Todo created = JsonUtils.fromResponse(response, Todo.class);
        Assert.assertNotNull(created.getId(), "Created todo should be assigned an id");
        Assert.assertEquals(created.getTitle(),     newTodo.getTitle(),     "Title should match");
        Assert.assertEquals(created.getUserId(),    newTodo.getUserId(),    "UserId should match");
        Assert.assertEquals(created.getCompleted(), newTodo.getCompleted(), "Completed flag should match");

        log.info("Todo created with id={}", created.getId());
    }

    // ─── Test 4: PATCH /todos/{id} ────────────────────────────────────────────

    @Test(description = "PATCH /todos/{id} should partially update the completed status",
          groups = {"regression"})
    @Story("Partial Update Todo")
    @Severity(SeverityLevel.NORMAL)
    public void patchTodo_shouldUpdateCompletedFlag() {

        int todoId = 1;
        // Partial payload — only the field we want to change
        String patchBody = "{\"completed\": true}";

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", todoId)
                .body(patchBody)
            .when()
                .patch(ApiConstants.Todos.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        Todo patched = JsonUtils.fromResponse(response, Todo.class);
        Assert.assertEquals(patched.getId(), todoId, "Todo id should remain unchanged");
        Assert.assertTrue(patched.getCompleted(), "completed flag should be true after PATCH");

        log.info("Todo id={} patched — completed={}", patched.getId(), patched.getCompleted());
    }

    // ─── Test 5: DataProvider — GET /todos/{id} for multiple IDs ─────────────

    @DataProvider(name = "todoIds")
    public Object[][] provideTodoIds() {
        return new Object[][] {
            {1},
            {5},
            {10},
            {20}
        };
    }

    @Test(dataProvider = "todoIds",
          description = "GET /todos/{id} should return a valid todo for each provided ID",
          groups = {"regression"})
    @Story("Get Todo By ID — Data-Driven")
    @Severity(SeverityLevel.NORMAL)
    public void getTodoById_dataProvider_shouldReturnCorrectTodo(int todoId) {

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", todoId)
            .when()
                .get(ApiConstants.Todos.BY_ID)
            .then()
                .extract().response();

        ResponseValidator.assertStatusCode(response, ApiConstants.STATUS_OK);

        Todo todo = JsonUtils.fromResponse(response, Todo.class);
        Assert.assertEquals(todo.getId(), todoId,
                "Returned todo id does not match requested id=" + todoId);
        Assert.assertFalse(todo.getTitle().isBlank(), "Title should not be blank for id=" + todoId);

        log.info("Todo id={} → title='{}', completed={}", todo.getId(), todo.getTitle(), todo.getCompleted());
    }
}
