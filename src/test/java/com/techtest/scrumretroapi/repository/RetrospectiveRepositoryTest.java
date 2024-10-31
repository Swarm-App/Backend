package com.techtest.scrumretroapi.repository;

import com.techtest.scrumretroapi.entity.Retrospective;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RetrospectiveRepositoryTest {
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
    void testFindAllRetrospectives() {
        // Mock data
        List<Retrospective> retrospectives = new ArrayList<>();
        Retrospective retrospective1 = new Retrospective();
        retrospective1.setName("Retrospective 1");
        retrospectives.add(retrospective1);

        Page<Retrospective> pagedRetrospectives = new PageImpl<>(retrospectives);

        // Stubbing the method call
        when(retrospectiveRepository.findAllRetrospectives(any(Pageable.class))).thenReturn(pagedRetrospectives);

        // Call the service method
        Page<Retrospective> retrievedRetrospectives = retrospectiveService.getAllRetrospectives(Pageable.unpaged());

        // Assertion
        assertEquals(1, retrievedRetrospectives.getContent().size());
        assertEquals("Retrospective 1", retrievedRetrospectives.getContent().get(0).getName());
    }

    @Test
    void testFindRetrospectivesByDate() {
        // Given
        LocalDate date = LocalDate.now();
        Retrospective retrospective1 = new Retrospective();
        retrospective1.setName("Retrospective 1");
        retrospective1.setDate(date);

        Retrospective retrospective2 = new Retrospective();
        retrospective2.setName("Retrospective 2");
        retrospective2.setDate(date.plusDays(1));
        List<Retrospective> expectedRetrospectives = Arrays.asList(retrospective1, retrospective2);

        when(retrospectiveRepository.findRetrospectivesByDate(date)).thenReturn(expectedRetrospectives);

        // When
        List<Retrospective> actualRetrospectives = retrospectiveService.getRetrospectivesByDate(date);

        // Then
        assertEquals(expectedRetrospectives.size(), actualRetrospectives.size());
        assertEquals(expectedRetrospectives, actualRetrospectives);
    }

    @Test
    void testFindByName() {
        // Mock data
        String retrospectiveName = "Retrospective 1";
        Retrospective retrospective = new Retrospective();
        retrospective.setName(retrospectiveName);

        // Stubbing the method call
        when(retrospectiveRepository.findByName(retrospectiveName)).thenReturn(retrospective);
        Retrospective outputRetro = retrospectiveRepository.findByName(retrospectiveName);

        // Assertion
        assertEquals(retrospectiveName, outputRetro.getName());
    }
}
