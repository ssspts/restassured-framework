package com.api.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Utility class for JSON serialisation / deserialisation using Jackson.
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {}

    /**
     * Converts any object to a JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", obj, e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * Deserialises a JSON string into the given class type.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON: {}", json, e);
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }

    /**
     * Deserialises a Response body into the given class type.
     */
    public static <T> T fromResponse(Response response, Class<T> clazz) {
        return fromJson(response.asString(), clazz);
    }

    /**
     * Deserialises a Response body into a List of the given type.
     */
    public static <T> List<T> listFromResponse(Response response, Class<T> clazz) {
        try {
            return objectMapper.readValue(
                    response.asString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize response list: {}", response.asString(), e);
            throw new RuntimeException("JSON list deserialization failed", e);
        }
    }

    /**
     * Deserialises a JSON string using a TypeReference (for generics).
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON with TypeReference: {}", json, e);
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }
}
