package com.conference.management_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conference.management_system.dto.RegistrationResponse;
import com.conference.management_system.entity.Registration;
import com.conference.management_system.entity.Session;
import com.conference.management_system.entity.User;
import com.conference.management_system.exception.ApiException;
import com.conference.management_system.repository.RegistrationRepository;
import com.conference.management_system.repository.SessionRepository;
import com.conference.management_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    
    private final RegistrationRepository registrationRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public RegistrationResponse registerForSession(Long sessionId) {
        log.info("Register for session attempt: sessionId={}", sessionId);
        User currentUser = getCurrentUser();
        log.info("Current user: id={}, username={}", currentUser.getId(), currentUser.getUsername());
        
        // Check if already registered
        if (registrationRepository.existsByUserIdAndSessionId(currentUser.getId(), sessionId)) {
            log.warn("User already registered for session: userId={}, sessionId={}", currentUser.getId(), sessionId);
            throw ApiException.conflict("Already registered for this session");
        }
        
        // Get session with PESSIMISTIC_WRITE lock to prevent race condition
        // This ensures only one thread can modify participant count at a time
        Session session = sessionRepository.findByIdWithLock(sessionId)
                .orElseThrow(() -> {
                    log.error("Session not found: sessionId={}", sessionId);
                    return ApiException.notFound("Session not found");
                });
        log.info("Session found and locked: id={}, title={}, current={}/{}", 
                session.getId(), session.getTitle(), 
                session.getCurrentParticipants(), session.getMaxParticipants());
        
        // Check if session is full - this check is now atomic with the increment
        if (session.getCurrentParticipants() >= session.getMaxParticipants()) {
            log.warn("Session is full: sessionId={}", sessionId);
            throw ApiException.badRequest("Session is full");
        }
        
        // Check for time conflicts with user's other registrations
        LocalDateTime sessionEnd = session.getSessionTime().plusMinutes(session.getDurationMinutes());
        List<Registration> conflicts = registrationRepository.findUserRegistrationConflicts(
                currentUser.getId(),
                session.getSessionTime(),
                sessionEnd
        );
        
        if (!conflicts.isEmpty()) {
            log.warn("Time conflict detected: userId={}, sessionId={}", currentUser.getId(), sessionId);
            throw ApiException.conflict("You have another session at this time");
        }
        
        // Create registration
        Registration registration = new Registration();
        registration.setUser(currentUser);
        registration.setSession(session);
        registration.setStatus(Registration.RegistrationStatus.CONFIRMED);
        
        Registration saved = registrationRepository.save(registration);
        log.info("Registration created: id={}, userId={}, sessionId={}", 
                saved.getId(), currentUser.getId(), sessionId);
        
        // Increment participant count
        session.setCurrentParticipants(session.getCurrentParticipants() + 1);
        sessionRepository.save(session);
        log.info("Session participants updated: sessionId={}, newCount={}", 
                sessionId, session.getCurrentParticipants());
        
        return mapToResponse(saved);
    }
    
    public List<RegistrationResponse> getMyRegistrations() {
        User currentUser = getCurrentUser();
        return registrationRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RegistrationResponse> getSessionRegistrations(Long sessionId) {
        return registrationRepository.findBySessionId(sessionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> ApiException.notFound("Registration not found"));
        
        User currentUser = getCurrentUser();
        if (!registration.getUser().getId().equals(currentUser.getId())) {
            throw ApiException.forbidden("You can only cancel your own registrations");
        }
        
        if (registration.getStatus() == Registration.RegistrationStatus.CANCELLED) {
            throw ApiException.conflict("Registration already cancelled");
        }
        
        // Update status
        registration.setStatus(Registration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        
        // Decrement participant count
        Session session = registration.getSession();
        session.setCurrentParticipants(Math.max(0, session.getCurrentParticipants() - 1));
        sessionRepository.save(session);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("getCurrentUser: username={}, principal={}", username, authentication.getPrincipal());
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found in database: username={}", username);
                    return ApiException.notFound("User not found");
                });
    }
    
    private RegistrationResponse mapToResponse(Registration registration) {
        RegistrationResponse response = new RegistrationResponse();
        response.setId(registration.getId());
        response.setUserId(registration.getUser().getId());
        response.setUsername(registration.getUser().getUsername());
        response.setSessionId(registration.getSession().getId());
        response.setSessionTitle(registration.getSession().getTitle());
        response.setSessionTime(registration.getSession().getSessionTime());
        response.setStatus(registration.getStatus());
        response.setRegisteredAt(registration.getRegisteredAt());
        return response;
    }
}
