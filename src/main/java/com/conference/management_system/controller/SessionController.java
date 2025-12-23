package com.conference.management_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.management_system.dto.SessionRequest;
import com.conference.management_system.dto.SessionResponse;
import com.conference.management_system.service.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "APIs for managing conference sessions")
public class SessionController {
    
    private final SessionService sessionService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Create a new session", description = "Create a new conference session (Coordinator/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid session data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.createSession(request));
    }
    
    @GetMapping
    @Operation(summary = "Get all sessions", description = "Retrieve list of all conference sessions")
    @ApiResponse(responseCode = "200", description = "List of sessions returned")
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming sessions", description = "Retrieve sessions scheduled for the future")
    @ApiResponse(responseCode = "200", description = "List of upcoming sessions returned")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessions() {
        return ResponseEntity.ok(sessionService.getUpcomingSessions());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID", description = "Retrieve details of a specific session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session details returned"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get my sessions", description = "Retrieve sessions for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User's sessions returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<SessionResponse>> getMySessions() {
        return ResponseEntity.ok(sessionService.getMySessions());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Update a session", description = "Modify details of an existing session (Coordinator/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session updated successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody SessionRequest request) {
        return ResponseEntity.ok(sessionService.updateSession(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Delete a session", description = "Remove a session from the system (Coordinator/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
