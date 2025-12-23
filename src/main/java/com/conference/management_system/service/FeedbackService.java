package com.conference.management_system.service;

import com.conference.management_system.dto.FeedbackRequest;
import com.conference.management_system.dto.FeedbackResponse;
import com.conference.management_system.entity.Feedback;
import com.conference.management_system.entity.Registration;
import com.conference.management_system.entity.Session;
import com.conference.management_system.entity.User;
import com.conference.management_system.exception.ApiException;
import com.conference.management_system.repository.FeedbackRepository;
import com.conference.management_system.repository.RegistrationRepository;
import com.conference.management_system.repository.SessionRepository;
import com.conference.management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        User currentUser = getCurrentUser();
        
        // Check if user already gave feedback for this session
        Optional<Feedback> existing = feedbackRepository.findByUserIdAndSessionId(
                currentUser.getId(), request.getSessionId());
        if (existing.isPresent()) {
            throw ApiException.conflict("You already gave feedback for this session");
        }
        
        // Check if user attended the session
        Optional<Registration> registration = registrationRepository.findByUserIdAndSessionId(
                currentUser.getId(), request.getSessionId());
        if (registration.isEmpty()) {
            throw ApiException.forbidden("You must be registered for this session to give feedback");
        }
        
        // Get session
        Session session = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> ApiException.notFound("Session not found"));
        
        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setUser(currentUser);
        feedback.setSession(session);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        
        Feedback saved = feedbackRepository.save(feedback);
        return mapToResponse(saved);
    }
    
    public List<FeedbackResponse> getSessionFeedback(Long sessionId) {
        return feedbackRepository.findBySessionId(sessionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<FeedbackResponse> getMyFeedback() {
        User currentUser = getCurrentUser();
        return feedbackRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Double getSessionAverageRating(Long sessionId) {
        Double average = feedbackRepository.getAverageRatingBySessionId(sessionId);
        return average != null ? average : 0.0;
    }
    
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> ApiException.notFound("Feedback not found"));
        
        feedbackRepository.delete(feedback);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> ApiException.notFound("User not found"));
    }
    
    private FeedbackResponse mapToResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setUserId(feedback.getUser().getId());
        response.setUsername(feedback.getUser().getUsername());
        response.setSessionId(feedback.getSession().getId());
        response.setSessionTitle(feedback.getSession().getTitle());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        response.setCreatedAt(feedback.getCreatedAt());
        return response;
    }
}
