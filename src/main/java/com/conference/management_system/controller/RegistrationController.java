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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Registrations", description = "APIs for managing session registrations")
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    @PostMapping("/session/{sessionId}")
    @Operation(summary = "Register for session", description = "Register the current user for a specific session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "409", description = "Already registered for this session")
    })
    @SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Get my registrations", description = "Retrieve all sessions the current user is registered for")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Get session registrations", description = "Get all users registered for a session (Coordinator/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations returned"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RegistrationResponse>> getSessionRegistrations(@PathVariable Long sessionId) {
        log.info("Get session registrations request: sessionId={}", sessionId);
        return ResponseEntity.ok(registrationService.getSessionRegistrations(sessionId));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel registration", description = "Unregister from a session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Registration cancelled successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Registration not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long id) {
        log.info("Cancel registration request: registrationId={}", id);
        registrationService.cancelRegistration(id);
        return ResponseEntity.noContent().build();
    }
}

