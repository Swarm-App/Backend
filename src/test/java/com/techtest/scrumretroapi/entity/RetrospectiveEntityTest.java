package com.techtest.scrumretroapi.entity;

import com.techtest.scrumretroapi.entity.feedback.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases to verify the creation and initialization of Retrospective objects using Lombok annotations,
 * ensuring that objects can be formatted into JSON.
 */

public class RetrospectiveEntityTest extends EntityBaseTest {
    private final String name = "Fixed bug";
    private final String summary = "A bug was fixed";
    private final LocalDate date = LocalDate.now();
    private final List<String> participants = new ArrayList<>(List.of("Tom", "Richard", "Harry"));
    private final List<Feedback> feedback = new ArrayList<>();

    private Retrospective retrospective;

    @BeforeEach
    void setUp() {
        this.retrospective = new Retrospective(name, summary, date, participants, feedback);
    }

    @Test
    void testObjectInitialisationWithConstructor() {
        // Read the data from the object and verify that it matches the input
        assertEquals(name, retrospective.getName());
        assertEquals(summary, retrospective.getSummary());
        assertEquals(date, retrospective.getDate());
        assertEquals(participants, retrospective.getParticipants());
        //assertEquals(feedback, retrospective.getFeedback());
    }

    @Test
    void testObjectIsConvertedToJsonWithJsonPropertyAnnotations() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // this module is needed to convert Java Time object to JSON string
        objectMapper.registerModule(new JavaTimeModule());

        // expected json string pattern for retrospective object
        String expectedJsonPattern = "\\{\"name\":\".*?\",\"summary\":\".*?\",\"date\":\"\\d{4}-\\d{2}-\\d{2}\",\"participants\":\\[\".*?\"],\"feedback\":\\[.*?\\]\\}";

        testObjectIsConvertedToJsonWithJsonPropertyAnnotations(retrospective, objectMapper, expectedJsonPattern);
    }
}
