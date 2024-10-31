package com.techtest.scrumretroapi.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtest.scrumretroapi.entity.feedback.Feedback;
import com.techtest.scrumretroapi.entity.feedback.FeedbackItem;
import com.techtest.scrumretroapi.entity.feedback.FeedbackType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedbackEntityTest extends EntityBaseTest {
    private final int itemId = 1;
    private Feedback feedback;
    private FeedbackItem feedbackItem;

    @BeforeEach
    void setUp() {
        this.feedbackItem = new FeedbackItem("John Smith", "Great, you fixed the bug!", FeedbackType.POSITIVE);
        this.feedback = new Feedback(itemId, feedbackItem);
    }

    @Test
    void testObjectInitialisationWithConstructor() {
        // Read the data from the object and verify that it matches the input
        assertEquals(itemId, feedback.getItem());
        assertEquals(feedbackItem, feedback.getItemBody());
    }

    @Test
    void testObjectIsConvertedToJsonWithJsonPropertyAnnotations() {
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJsonPattern = "\\{\"item\":\\d+,\"itemBody\":\\{\"name\":\".*?\",\"body\":\".*?\",\"feedbackType\":\".*?\"\\}\\}";

        testObjectIsConvertedToJsonWithJsonPropertyAnnotations(feedback, objectMapper, expectedJsonPattern);
    }
}
