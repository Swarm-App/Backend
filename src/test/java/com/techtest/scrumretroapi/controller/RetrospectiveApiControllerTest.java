package com.techtest.scrumretroapi.controller;

import com.techtest.scrumretroapi.entity.Retrospective;
import com.techtest.scrumretroapi.entity.feedback.FeedbackItem;
import com.techtest.scrumretroapi.service.RetrospectiveService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
public class RetrospectiveApiControllerTest {
    private AutoCloseable closeable;

    @Mock
    private RetrospectiveService retrospectiveService;

    @InjectMocks
    private RetrospectiveApiController retrospectiveApiController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetAllRetrospectivesSuccess() {
        // Mocking the service method to return a non-empty Optional with some retrospectives
        List<Retrospective> retrospectives = List.of(
                new Retrospective("Retrospective 1", "Post release retrospective", LocalDate.now(), new ArrayList<>(), new ArrayList<>()),
                new Retrospective("Retrospective 1", "Post release retrospective", LocalDate.now(), new ArrayList<>(), new ArrayList<>())
        );

        // factor in pagination
        Page<Retrospective> page = new PageImpl<>(retrospectives);

        when(retrospectiveService.getAllRetrospectives(any(Pageable.class))).thenReturn(page);

        // Call the controller method
        ResponseEntity<Page<Retrospective>> result = retrospectiveApiController.getAllRetrospectives(0, 10); // Example: First page with 10 items

        // Verifying the result and that the service is called
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(retrospectives, Objects.requireNonNull(result.getBody()).getContent()); // Verifying the returned retrospectives
        verify(retrospectiveService, times(1)).getAllRetrospectives(any(Pageable.class));
    }


    @Test
    void testGetAllRetrospectivesFail() {
        // Mocking the service method to return an empty Page
        Page<Retrospective> page = new PageImpl<>(Collections.emptyList());
        when(retrospectiveService.getAllRetrospectives(any(Pageable.class))).thenReturn(page);

        // Call the controller method
        ResponseEntity<Page<Retrospective>> result = retrospectiveApiController.getAllRetrospectives(0, 10); // Example: First page with 10 items

        // Verifying the result and that the service is called
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(retrospectiveService, times(1)).getAllRetrospectives(any(Pageable.class));
    }

    @Test
    void testGetAllRetrospectivesByDateSuccess() {
        // Mock the service method
        LocalDate date = LocalDate.now();
        List<Retrospective> retrospectives = Collections.singletonList(new Retrospective());
        when(retrospectiveService.getRetrospectivesByDate(date)).thenReturn(retrospectives);

        // Call the controller method
        ResponseEntity<List<Retrospective>> result = retrospectiveApiController.getAllRetrospectivesByDate(date);

        // Verifying the result and that the service is called
        assertEquals(OK, result.getStatusCode());
        verify(retrospectiveService, times(1)).getRetrospectivesByDate(date);
    }

    @Test
    void testGetAllRetrospectivesByDateFail() {
        // Mock the service method
        LocalDate date = LocalDate.now();
        when(retrospectiveService.getRetrospectivesByDate(date)).thenReturn(new ArrayList<>());

        // Call the controller method
        ResponseEntity<List<Retrospective>> result = retrospectiveApiController.getAllRetrospectivesByDate(date);

        // Verifying the result and that the service is called
        assertEquals(NOT_FOUND, result.getStatusCode());
        verify(retrospectiveService, times(1)).getRetrospectivesByDate(date);
    }

    @Test
    public void testCreateNewRetrospective() {
        // Mock the service method
        Retrospective retrospective = new Retrospective();

        try {
            doNothing().when(retrospectiveService).createNewRetrospective(retrospective);

            // Call the controller method
            ResponseEntity<String> result = retrospectiveApiController.createNewRetrospective(retrospective);

            // Verify the service method was called
            verify(retrospectiveService, times(1)).createNewRetrospective(retrospective);

            // Verify the result
            assertEquals(HttpStatus.OK, result.getStatusCode());
        } catch (Exception exp) {
            // Display the error message and fail the test
            System.err.println("Exception thrown: " + exp.getMessage());
            fail();
        }
    }

    @Test
    public void testAddFeedbackToRetrospective() {
        // Mock the service method
        FeedbackItem feedbackItem = new FeedbackItem();

        try {
            doNothing().when(retrospectiveService).createNewFeedbackForRetrospective(anyString(), eq(feedbackItem));

            // Call the controller method
            ResponseEntity<Void> result = retrospectiveApiController.addFeedbackToRetrospective("name", feedbackItem);

            // Verify the service method was called
            verify(retrospectiveService, times(1)).createNewFeedbackForRetrospective("name", feedbackItem);

            // Verify the result
            assertEquals(HttpStatus.OK, result.getStatusCode());
        } catch (Exception exp) {
            // Display the error message and fail the test
            System.err.println("Exception thrown: " + exp.getMessage());
            fail();
        }
    }

    @Test
    public void testUpdateFeedbackItem() {
        // Mock the service method
        FeedbackItem feedbackItem = new FeedbackItem();

        try {
            doNothing().when(retrospectiveService).updateFeedbackForRetrospective(anyString(), anyInt(), eq(feedbackItem));

            // Call the controller method
            ResponseEntity<Void> result = retrospectiveApiController.updateFeedbackItem("name", 1, feedbackItem);

            // Verify the service method was called
            verify(retrospectiveService, times(1)).updateFeedbackForRetrospective("name", 1, feedbackItem);

            // Verify the result
            assertEquals(HttpStatus.OK, result.getStatusCode());
        } catch (Exception exp) {
            // Display the error message and fail the test
            System.err.println("Exception thrown: " + exp.getMessage());
            fail();
        }
    }
}
