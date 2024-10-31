package com.techtest.scrumretroapi.entity.integration;

import com.techtest.scrumretroapi.entity.Retrospective;
import com.techtest.scrumretroapi.entity.feedback.Feedback;
import com.techtest.scrumretroapi.entity.feedback.FeedbackItem;
import com.techtest.scrumretroapi.entity.feedback.FeedbackType;
import com.techtest.scrumretroapi.repository.RetrospectiveRepository;
import com.techtest.scrumretroapi.service.RetrospectiveService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EntityJpaIntegrationTest {
    private AutoCloseable closeable;

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private RetrospectiveService retrospectiveService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * Test demonstrates the integration of objects when saved in the JPA repository, where FeedbackItem
     * is embedded with Feedback, which is embedded in Retrospective. It is also able to persist state (see Repository test).
     */
    @Test
    void testRetrospectiveWithFeedbackData() {
        Retrospective inputRetro = new Retrospective();
        inputRetro.setName("Retrospective 1");
        inputRetro.setSummary("Summary");
        inputRetro.setDate(LocalDate.now());
        inputRetro.setParticipants(Arrays.asList("Person1", "Person2", "Person3"));

        // create new Feedback ArrayList to add to retrospective
        List<Feedback> feedbackList = Arrays.asList(
            new Feedback(1, new FeedbackItem("Item 1", "Body 1", FeedbackType.POSITIVE)),
            new Feedback(2, new FeedbackItem("Item 2", "Body 2", FeedbackType.NEGATIVE)),
            new Feedback(3, new FeedbackItem("Item 3", "Body 3", FeedbackType.IDEA))
        );

        inputRetro.setFeedback(feedbackList);

        // Stub repository method to return the mocked retrospective
        when(retrospectiveRepository.findRetrospectivesByDate(LocalDate.now())).thenReturn(List.of(inputRetro));

        // Call service method to get retrospective feedback
        List<Retrospective> retrievedRetrospectiveList = retrospectiveService.getRetrospectivesByDate(LocalDate.now());

        // Assertions
        assertNotNull(retrievedRetrospectiveList);

        Retrospective outputRetro = retrievedRetrospectiveList.stream().findFirst().orElseThrow();

        // check that input and output objects are like for like
        assertEquals(inputRetro.getName(), outputRetro.getName());
        assertEquals(inputRetro.getSummary(), outputRetro.getSummary());
        assertEquals(inputRetro.getDate(), outputRetro.getDate());
        assertEquals(inputRetro.getParticipants().size(), outputRetro.getParticipants().size());
        assertEquals(inputRetro.getFeedback().size(), outputRetro.getFeedback().size());
    }
}
