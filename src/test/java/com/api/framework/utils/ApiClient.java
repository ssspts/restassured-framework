package com.api.framework.utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper around RestAssured verbs.
 * Keeps test classes free from repetitive given/when/then boilerplate
 * while still giving full access to the underlying Response.
 */
public class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);
    private final RequestSpecification spec;

    public ApiClient(RequestSpecification spec) {
        this.spec = spec;
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    public Response get(String path) {
        log.debug("GET {}", path);
        return given().spec(spec).when().get(path).then().extract().response();
    }

    public Response get(String path, Map<String, Object> pathParams) {
        log.debug("GET {} — params={}", path, pathParams);
        return given().spec(spec).pathParams(pathParams)
                .when().get(path).then().extract().response();
    }

    public Response getWithQueryParams(String path, Map<String, Object> queryParams) {
        log.debug("GET {} — query={}", path, queryParams);
        return given().spec(spec).queryParams(queryParams)
                .when().get(path).then().extract().response();
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    public Response post(String path, Object body) {
        log.debug("POST {}", path);
        return given().spec(spec).body(JsonUtils.toJson(body))
                .when().post(path).then().extract().response();
    }

    // ─── PUT ──────────────────────────────────────────────────────────────────

    public Response put(String path, Map<String, Object> pathParams, Object body) {
        log.debug("PUT {} — params={}", path, pathParams);
        return given().spec(spec).pathParams(pathParams).body(JsonUtils.toJson(body))
                .when().put(path).then().extract().response();
    }

    // ─── PATCH ────────────────────────────────────────────────────────────────

    public Response patch(String path, Map<String, Object> pathParams, Object body) {
        log.debug("PATCH {} — params={}", path, pathParams);
        return given().spec(spec).pathParams(pathParams).body(JsonUtils.toJson(body))
                .when().patch(path).then().extract().response();
    }

    public Response patch(String path, Map<String, Object> pathParams, String rawBody) {
        log.debug("PATCH {} — params={}", path, pathParams);
        return given().spec(spec).pathParams(pathParams).body(rawBody)
                .when().patch(path).then().extract().response();
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public Response delete(String path, Map<String, Object> pathParams) {
        log.debug("DELETE {} — params={}", path, pathParams);
        return given().spec(spec).pathParams(pathParams)
                .when().delete(path).then().extract().response();
    }
}
