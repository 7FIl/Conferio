package com.conference.management_system.controller;

import com.conference.management_system.dto.FeedbackRequest;
import com.conference.management_system.dto.FeedbackResponse;
import com.conference.management_system.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.createFeedback(request));
    }
    
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<FeedbackResponse>> getSessionFeedback(@PathVariable Long sessionId) {
        return ResponseEntity.ok(feedbackService.getSessionFeedback(sessionId));
    }
    
    @GetMapping("/session/{sessionId}/average")
    public ResponseEntity<Double> getSessionAverageRating(@PathVariable Long sessionId) {
        return ResponseEntity.ok(feedbackService.getSessionAverageRating(sessionId));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback() {
        return ResponseEntity.ok(feedbackService.getMyFeedback());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
