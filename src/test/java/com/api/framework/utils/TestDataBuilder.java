package com.api.framework.utils;

import com.api.framework.models.Post;
import com.api.framework.models.Todo;
import com.api.framework.models.User;
import com.github.javafaker.Faker;

public class TestDataBuilder {

    private static final Faker faker = new Faker();

    private TestDataBuilder() {}

    public static Post buildPost() {
        return Post.builder()
                .userId(faker.number().numberBetween(1, 10))
                .title(faker.lorem().sentence(5))
                .body(faker.lorem().paragraph(2))
                .build();
    }

    public static Post buildPost(int userId) {
        return Post.builder()
                .userId(userId)
                .title(faker.lorem().sentence(5))
                .body(faker.lorem().paragraph(2))
                .build();
    }

    public static Post buildPostWithBlankTitle() {
        return Post.builder()
                .userId(1)
                .title("")
                .body(faker.lorem().paragraph(1))
                .build();
    }

    public static User buildUser() {
        return User.builder()
                .name(faker.name().fullName())
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .website(faker.internet().domainName())
                .build();
    }

    public static Todo buildTodo() {
        return Todo.builder()
                .userId(faker.number().numberBetween(1, 10))
                .title(faker.lorem().sentence(4))
                .completed(faker.bool().bool())
                .build();
    }

    public static Todo buildCompletedTodo(int userId) {
        return Todo.builder()
                .userId(userId)
                .title(faker.lorem().sentence(4))
                .completed(true)
                .build();
    }
}
