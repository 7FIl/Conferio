package com.conference.management_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.management_system.dto.RegistrationResponse;
import com.conference.management_system.service.RegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<RegistrationResponse> registerForSession(@PathVariable Long sessionId) {
        log.info("Register for session request: sessionId={}", sessionId);
        try {
            RegistrationResponse response = registrationService.registerForSession(sessionId);
            log.info("Registration successful: sessionId={}", sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed: sessionId={}, error={}", sessionId, e.getMessage());
            throw e;
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<RegistrationResponse>> getMyRegistrations() {
        log.info("Get my registrations request");
        try {
            List<RegistrationResponse> registrations = registrationService.getMyRegistrations();
            log.info("Returned {} registrations", registrations.size());
            return ResponseEntity.ok(registrations);
        } catch (Exception e) {
            log.error("Get registrations failed: error={}", e.getMessage());
            throw e;
        }
    }
    
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    public ResponseEntity<List<RegistrationResponse>> getSessionRegistrations(@PathVariable Long sessionId) {
        log.info("Get session registrations request: sessionId={}", sessionId);
        return ResponseEntity.ok(registrationService.getSessionRegistrations(sessionId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long id) {
        log.info("Cancel registration request: registrationId={}", id);
        registrationService.cancelRegistration(id);
        return ResponseEntity.noContent().build();
    }
}

