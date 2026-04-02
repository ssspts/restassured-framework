package com.api.framework.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("completed")
    private Boolean completed;

    public Todo() {}

    public Todo(Integer id, Integer userId, String title, Boolean completed) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.completed = completed;
    }

    public Integer getId()                    { return id; }
    public void setId(Integer id)             { this.id = id; }
    public Integer getUserId()                { return userId; }
    public void setUserId(Integer userId)     { this.userId = userId; }
    public String getTitle()                  { return title; }
    public void setTitle(String title)        { this.title = title; }
    public Boolean getCompleted()             { return completed; }
    public void setCompleted(Boolean c)       { this.completed = c; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer id;
        private Integer userId;
        private String title;
        private Boolean completed;

        public Builder id(Integer id)             { this.id = id; return this; }
        public Builder userId(Integer userId)     { this.userId = userId; return this; }
        public Builder title(String title)        { this.title = title; return this; }
        public Builder completed(Boolean c)       { this.completed = c; return this; }
        public Todo build() { return new Todo(id, userId, title, completed); }
    }
}
