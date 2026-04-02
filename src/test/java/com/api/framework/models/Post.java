package com.api.framework.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    public Post() {}

    public Post(Integer id, Integer userId, String title, String body) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    public Integer getId()                { return id; }
    public void setId(Integer id)         { this.id = id; }
    public Integer getUserId()            { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTitle()              { return title; }
    public void setTitle(String title)    { this.title = title; }
    public String getBody()               { return body; }
    public void setBody(String body)      { this.body = body; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer id;
        private Integer userId;
        private String title;
        private String body;

        public Builder id(Integer id)         { this.id = id; return this; }
        public Builder userId(Integer userId) { this.userId = userId; return this; }
        public Builder title(String title)    { this.title = title; return this; }
        public Builder body(String body)      { this.body = body; return this; }
        public Post build() { return new Post(id, userId, title, body); }
    }
}
