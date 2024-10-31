package com.techtest.scrumretroapi.service;

import com.techtest.scrumretroapi.entity.Retrospective;
import com.techtest.scrumretroapi.entity.feedback.Feedback;
import com.techtest.scrumretroapi.entity.feedback.FeedbackItem;
import com.techtest.scrumretroapi.repository.RetrospectiveRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RetrospectiveServiceTest {
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

    @Test
    void testGetAllRetrospectives() {
        // Arrange
        List<Retrospective> retrospectives = new ArrayList<>();
        Page<Retrospective> page = new PageImpl<>(retrospectives);
        when(retrospectiveRepository.findAllRetrospectives(any(Pageable.class))).thenReturn(page);

        // Act
        Page<Retrospective> result = retrospectiveService.getAllRetrospectives(Pageable.unpaged());

        // Assert
        verify(retrospectiveRepository, times(1)).findAllRetrospectives(any(Pageable.class));
        assertNotNull(result);
        assertEquals(retrospectives, result.getContent());
    }

    @Test
    void testGetRetrospectivesByDate() {
        // Arrange
        LocalDate date = LocalDate.now();
        List<Retrospective> retrospectives = new ArrayList<>();
        when(retrospectiveRepository.findRetrospectivesByDate(date)).thenReturn(retrospectives);

        // Act
       List<Retrospective> result = retrospectiveService.getRetrospectivesByDate(date);

        // Assert
        verify(retrospectiveRepository, times(1)).findRetrospectivesByDate(date);
        assertSame(retrospectives, result);
    }

    @Test
    void testCreateNewRetrospective() {
        // Mock the repository method
        Retrospective retrospective = new Retrospective();
        when(retrospectiveRepository.save(retrospective)).thenReturn(retrospective);

        // Call the service method
        assertDoesNotThrow(() -> retrospectiveService.createNewRetrospective(retrospective));

        // Verify the repository method was called
        verify(retrospectiveRepository, times(1)).save(retrospective);
    }

    @Test
    void testCreateNewFeedbackForRetrospective() {
        // Mock the repository method
        Retrospective existingRetrospective = new Retrospective("retrospectiveName", "", LocalDate.now(), new ArrayList<>(), new ArrayList<>()); // Mock existing retrospective
        when(retrospectiveRepository.findByName("retrospectiveName")).thenReturn(existingRetrospective);

        // Call the service method
        FeedbackItem feedbackItem = new FeedbackItem();
        assertDoesNotThrow(() -> retrospectiveService.createNewFeedbackForRetrospective("retrospectiveName", feedbackItem));

        // Verify that save() method is called with the updated Retrospective object
        verify(retrospectiveRepository, times(1)).save(existingRetrospective);
    }

    @Test
    void testUpdateFeedbackForRetrospective() {
        // Mock the repository method
        Retrospective existingRetrospective = new Retrospective(); // Mock existing retrospective
        List<Feedback> feedbackList = new ArrayList<>(); // Initialize feedbackList
        existingRetrospective.setFeedback(feedbackList); // Set feedbackList to existingRetrospective
        when(retrospectiveRepository.findByName("retrospectiveName")).thenReturn(existingRetrospective);

        // Call the service method and assert that it throws the expected exception
        FeedbackItem newFeedbackItem = new FeedbackItem();
        Exception exception = assertThrows(Exception.class, () -> retrospectiveService.updateFeedbackForRetrospective("retrospectiveName", 1, newFeedbackItem));
        assertEquals("No feedback with item number: 1. Please revise item number or create new feedback", exception.getMessage());

        // Verify that save() method is not called with the updated Retrospective object
        verify(retrospectiveRepository, never()).save(existingRetrospective);
    }
}
