package com.conference.management_system.controller;

import com.conference.management_system.dto.RegistrationResponse;
import com.conference.management_system.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<RegistrationResponse> registerForSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(registrationService.registerForSession(sessionId));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<RegistrationResponse>> getMyRegistrations() {
        return ResponseEntity.ok(registrationService.getMyRegistrations());
    }
    
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    public ResponseEntity<List<RegistrationResponse>> getSessionRegistrations(@PathVariable Long sessionId) {
        return ResponseEntity.ok(registrationService.getSessionRegistrations(sessionId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return ResponseEntity.noContent().build();
    }
}
