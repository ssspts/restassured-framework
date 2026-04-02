package com.api.framework.constants;

/**
 * Central store for all API endpoint paths and common constants.
 * Use these instead of hardcoding strings in tests.
 */
public final class ApiConstants {

    private ApiConstants() {}

    // ─── HTTP Status Codes ─────────────────────────────────────────────────────
    public static final int STATUS_OK          = 200;
    public static final int STATUS_CREATED     = 201;
    public static final int STATUS_NO_CONTENT  = 204;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED= 401;
    public static final int STATUS_FORBIDDEN   = 403;
    public static final int STATUS_NOT_FOUND   = 404;
    public static final int STATUS_SERVER_ERR  = 500;

    // ─── JSONPlaceholder Endpoints (https://jsonplaceholder.typicode.com) ──────
    public static final class Posts {
        public static final String BASE        = "/posts";
        public static final String BY_ID       = "/posts/{id}";
        public static final String COMMENTS    = "/posts/{id}/comments";
    }

    public static final class Users {
        public static final String BASE        = "/users";
        public static final String BY_ID       = "/users/{id}";
        public static final String POSTS       = "/users/{id}/posts";
        public static final String TODOS       = "/users/{id}/todos";
    }

    public static final class Todos {
        public static final String BASE        = "/todos";
        public static final String BY_ID       = "/todos/{id}";
    }

    public static final class Comments {
        public static final String BASE        = "/comments";
        public static final String BY_ID       = "/comments/{id}";
    }

    // ─── Common Header Names ────────────────────────────────────────────────────
    public static final class Headers {
        public static final String CONTENT_TYPE   = "Content-Type";
        public static final String AUTHORIZATION  = "Authorization";
        public static final String ACCEPT         = "Accept";
        public static final String CORRELATION_ID = "X-Correlation-ID";
    }

    // ─── Content Types ───────────────────────────────────────────────────────────
    public static final String JSON_UTF8 = "application/json; charset=utf-8";
}
