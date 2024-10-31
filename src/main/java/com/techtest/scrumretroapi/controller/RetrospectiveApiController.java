package com.techtest.scrumretroapi.controller;

import com.techtest.scrumretroapi.entity.Retrospective;
import com.techtest.scrumretroapi.entity.feedback.FeedbackItem;
import com.techtest.scrumretroapi.service.RetrospectiveService;
import com.techtest.scrumretroapi.utils.LoggingUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@OpenAPIDefinition(
        info = @Info(
                title = "Scrum Retrospective API",
                description = "This template API project provides basic endpoints for reading, creating, editing and deleting user data."
        )
)
@RequestMapping("/retrospective")
@Tag(name = "Scrum Retrospective API")
public class RetrospectiveApiController {

    @Autowired
    private RetrospectiveService retrospectiveService;

    private final Log logger = LoggingUtil.getLogger(RetrospectiveApiController.class);

    @Operation(summary = "Get all retrospectives (with pagination)", description = "Get all retrospectives with pagination. Returns data in JSON or XML format based on the 'Accept' header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned all available retrospectives from the server",
                    content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Retrospective.class)),
                            @io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_XML_VALUE, schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Retrospective.class))}),
            @ApiResponse(responseCode = "404", description = "No retrospective data found"),
            @ApiResponse(responseCode = "500", description = "Failed to fetch retrospectives from server")
    })
    @GetMapping(value = "/all", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<Retrospective>> getAllRetrospectives(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        logger.info("API invoked: httpMethod=GET, path=/retrospectives/all");
        logger.info(String.format("Attempting to get all retrospectives with pagination page=%d, pageSize=%d", page, pageSize));

        // Set up pagination
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Retrospective> retrospectives = retrospectiveService.getAllRetrospectives(pageable);

        // check if any data is returned and return the response
        if (!retrospectives.isEmpty()) {
            logger.info("Successfully found all retrospectives. Returning status OK");
            return ResponseEntity.ok(retrospectives);
        } else {
            logger.warn("No retrospectives found. Returning status NOT FOUND");
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all retrospectives by date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned all available retrospectives for given date from the server",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Retrospective.class))}),
            @ApiResponse(responseCode = "404", description = "No retrospective data found for given date", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to fetch retrospectives from server", content = @Content)
    })
    @GetMapping("/filter")
    public ResponseEntity<List<Retrospective>> getAllRetrospectivesByDate(@Parameter(description = "Date to filter retrospectives by")
                                                                          @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info(String.format("API invoked: httpMethod=GET, path=retrospectives/filter?{date} (date=%s)", date.toString()));
        List<Retrospective> retrospectives = retrospectiveService.getRetrospectivesByDate(date);

        if (!retrospectives.isEmpty()) {
            logger.info("Successfully found all retrospectives with date: " + date + ". Returning status OK");
            return ResponseEntity.ok(retrospectives);
        } else {
            logger.warn("No retrospectives found with date " + date + ". Returning status NOT FOUND");
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new retrospective",
            description = "Endpoint to create a new retrospective to be updated in the server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New retrospective successfully created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to create new retrospective in server", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<String> createNewRetrospective(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New retrospective to be created.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Retrospective.class)
                    ))
            @RequestBody final Retrospective retrospective) {
        logger.info("API Invoked: httpMethod=POST, path='/retrospective'");
        logger.info("Attempting to create new retrospective with values: " + retrospective);

        try {
            retrospectiveService.createNewRetrospective(retrospective);
            logger.info("Successfully created new retrospective: " + retrospective.getName());
            return ResponseEntity.ok().body("Successfully created new retrospective");
        } catch (IllegalArgumentException exp) {
            logger.warn("Invalid request body received: " + exp.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
        } catch (Exception exp) {
            logger.error("Failed to create new retrospective: " + exp.getMessage(), exp);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to create new item. Process failed with exception: " + exp.getMessage());
        }
    }

    @Operation(summary = "Create feedback for existing retrospective")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New feedback successfully added to retrospective", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404", description = "Retrospective not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to create new feedback for retrospective in server", content = @Content)
    })
    @PutMapping("/{retrospectiveName}/feedback")
    public ResponseEntity<Void> addFeedbackToRetrospective(
            @PathVariable String retrospectiveName,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New feedback to be created for retrospective.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FeedbackItem.class)
                    ))
            @RequestBody FeedbackItem newFeedbackItem) {
        logger.info(String.format("API Invoked: httpMethod=PUT path='/retrospective/%s/feedback'", retrospectiveName));
        logger.info("Attempting to add new feedback for retrospective with values: " + newFeedbackItem);

        try {
            logger.info(String.format("Feedback for retrospective '%s' successfully added. Returning status OK", retrospectiveName));
            retrospectiveService.createNewFeedbackForRetrospective(retrospectiveName, newFeedbackItem);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exp) {
            logger.warn("Retrospective not found for name: " + retrospectiveName);
            return ResponseEntity.notFound().build();
        } catch (Exception exp) {
            logger.error("Failed to add feedback to retrospective: " + retrospectiveName, exp);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update feedback for retrospective")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback item successfully updated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404", description = "Feedback item not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to update feedback in server", content = @Content)
    })
    @PutMapping("{retrospectiveName}/feedback/{itemId}")
    public ResponseEntity<Void> updateFeedbackItem(
            @PathVariable String retrospectiveName,
            @PathVariable int itemId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Retrospective feedback to be edited",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FeedbackItem.class)
                    ))
            @RequestBody FeedbackItem feedbackItem) {
        logger.info(String.format("API Invoked: httpMethod=PUT path='/retrospective/%s/feedback/%d'", retrospectiveName, itemId));
        logger.info(String.format("Updating feedback for retrospective: %s, item ID: %d", retrospectiveName, itemId));
        logger.info("Attempting to update feedback for retrospective with values: " + feedbackItem);

        try {
            logger.info(String.format("Feedback for retrospective '%s', feedback item '%d' successfully updated. Returning status OK",
                    retrospectiveName, itemId));
            retrospectiveService.updateFeedbackForRetrospective(retrospectiveName, itemId, feedbackItem);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exp) {
            logger.warn("Feedback item not found for retrospective: " + retrospectiveName + ", item ID: " + itemId);
            return ResponseEntity.notFound().build();
        } catch (Exception exp) {
            logger.error("Failed to update feedback for retrospective: " + retrospectiveName + ", item ID: " + itemId, exp);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
