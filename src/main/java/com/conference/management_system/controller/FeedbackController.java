package com.conference.management_system.controller;

import com.conference.management_system.dto.FeedbackRequest;
import com.conference.management_system.dto.FeedbackResponse;
import com.conference.management_system.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "APIs for managing session feedback and ratings")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    @PostMapping
    @Operation(summary = "Submit feedback", description = "Submit feedback and rating for a session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feedback submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid feedback data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.createFeedback(request));
    }
    
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get session feedback", description = "Retrieve all feedback for a specific session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of feedback returned"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<List<FeedbackResponse>> getSessionFeedback(@PathVariable Long sessionId) {
        return ResponseEntity.ok(feedbackService.getSessionFeedback(sessionId));
    }
    
    @GetMapping("/session/{sessionId}/average")
    @Operation(summary = "Get session average rating", description = "Calculate the average rating for a session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average rating returned"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<Double> getSessionAverageRating(@PathVariable Long sessionId) {
        return ResponseEntity.ok(feedbackService.getSessionAverageRating(sessionId));
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get my feedback", description = "Retrieve feedback submitted by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User's feedback returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback() {
        return ResponseEntity.ok(feedbackService.getMyFeedback());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Delete feedback", description = "Remove feedback from the system (Coordinator/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Feedback deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
