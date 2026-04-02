package com.api.framework.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("website")
    private String website;

    public User() {}

    public User(Integer id, String name, String username, String email, String phone, String website) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.website = website;
    }

    public Integer getId()                  { return id; }
    public void setId(Integer id)           { this.id = id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }
    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }
    public String getPhone()                { return phone; }
    public void setPhone(String phone)      { this.phone = phone; }
    public String getWebsite()              { return website; }
    public void setWebsite(String website)  { this.website = website; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer id;
        private String name, username, email, phone, website;

        public Builder id(Integer id)           { this.id = id; return this; }
        public Builder name(String name)         { this.name = name; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email)       { this.email = email; return this; }
        public Builder phone(String phone)       { this.phone = phone; return this; }
        public Builder website(String website)   { this.website = website; return this; }
        public User build() { return new User(id, name, username, email, phone, website); }
    }
}
