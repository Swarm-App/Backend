package com.techtest.scrumretroapi.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base test class to provide the basic logic for the entity tests to avoid repeated code
 */

public abstract class EntityBaseTest {

    void testObjectIsConvertedToJsonWithJsonPropertyAnnotations(Object object, ObjectMapper objectMapper, String expectedJsonPattern) {
        try {
            String actualJsonString = objectMapper.writeValueAsString(object);
            System.out.println(actualJsonString);

            // Use regex pattern matching to verify that the produced JSON string is formatted correctly
            Pattern jsonPattern = Pattern.compile(expectedJsonPattern);
            Matcher matcher = jsonPattern.matcher(actualJsonString);

            assertTrue(matcher.matches());
        } catch (JsonProcessingException exp) {
            // if an exception is thrown, print the message and fail the test
            System.out.println("Failed to parse Object as JSON string: " + exp.getMessage());
            fail();
        }
    }
}
