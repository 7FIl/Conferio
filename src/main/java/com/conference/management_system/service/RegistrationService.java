package com.conference.management_system.service;

import com.conference.management_system.dto.RegistrationResponse;
import com.conference.management_system.entity.Registration;
import com.conference.management_system.entity.Session;
import com.conference.management_system.entity.User;
import com.conference.management_system.repository.RegistrationRepository;
import com.conference.management_system.repository.SessionRepository;
import com.conference.management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    
    private final RegistrationRepository registrationRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public RegistrationResponse registerForSession(Long sessionId) {
        User currentUser = getCurrentUser();
        
        // Check if already registered
        if (registrationRepository.existsByUserIdAndSessionId(currentUser.getId(), sessionId)) {
            throw new RuntimeException("Already registered for this session");
        }
        
        // Get session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // Check if session is full
        if (session.getCurrentParticipants() >= session.getMaxParticipants()) {
            throw new RuntimeException("Session is full");
        }
        
        // Check for time conflicts with user's other registrations
        LocalDateTime sessionEnd = session.getSessionTime().plusMinutes(session.getDurationMinutes());
        List<Registration> conflicts = registrationRepository.findUserRegistrationConflicts(
                currentUser.getId(),
                session.getSessionTime(),
                sessionEnd
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("You have another session at this time");
        }
        
        // Create registration
        Registration registration = new Registration();
        registration.setUser(currentUser);
        registration.setSession(session);
        registration.setStatus(Registration.RegistrationStatus.CONFIRMED);
        
        Registration saved = registrationRepository.save(registration);
        
        // Increment participant count
        session.setCurrentParticipants(session.getCurrentParticipants() + 1);
        sessionRepository.save(session);
        
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
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        
        User currentUser = getCurrentUser();
        if (!registration.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only cancel your own registrations");
        }
        
        if (registration.getStatus() == Registration.RegistrationStatus.CANCELLED) {
            throw new RuntimeException("Registration already cancelled");
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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
