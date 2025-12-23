package com.conference.management_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.management_system.dto.ProposalRequest;
import com.conference.management_system.dto.ProposalResponse;
import com.conference.management_system.dto.ProposalReviewRequest;
import com.conference.management_system.service.ProposalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@Tag(name = "Proposals", description = "APIs for managing conference proposals")
public class ProposalController {
    
    private final ProposalService proposalService;
    
    @PostMapping
    @Operation(summary = "Submit a new proposal", description = "Create a new conference proposal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proposal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid proposal data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProposalResponse> createProposal(@Valid @RequestBody ProposalRequest request) {
        return ResponseEntity.ok(proposalService.createProposal(request));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Get all proposals", description = "Retrieve all submitted proposals (Coordinator/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of proposals returned"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ProposalResponse>> getAllProposals() {
        return ResponseEntity.ok(proposalService.getAllProposals());
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get my proposals", description = "Retrieve proposals submitted by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User's proposals returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ProposalResponse>> getMyProposals() {
        return ResponseEntity.ok(proposalService.getMyProposals());
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Get proposals by status", description = "Filter proposals by their review status (PENDING, APPROVED, REJECTED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proposals with matching status returned"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ProposalResponse>> getProposalsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(proposalService.getProposalsByStatus(status));
    }
    
    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    @Operation(summary = "Review a proposal", description = "Submit a review decision (approve/reject) for a proposal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proposal reviewed successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Proposal not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProposalResponse> reviewProposal(
            @PathVariable Long id,
            @Valid @RequestBody ProposalReviewRequest request) {
        return ResponseEntity.ok(proposalService.reviewProposal(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a proposal", description = "Remove a proposal from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Proposal deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Proposal not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProposal(@PathVariable Long id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.noContent().build();
    }
}
