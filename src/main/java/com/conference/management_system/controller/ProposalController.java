package com.conference.management_system.controller;

import com.conference.management_system.dto.ProposalRequest;
import com.conference.management_system.dto.ProposalResponse;
import com.conference.management_system.dto.ProposalReviewRequest;
import com.conference.management_system.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {
    
    private final ProposalService proposalService;
    
    @PostMapping
    public ResponseEntity<ProposalResponse> createProposal(@Valid @RequestBody ProposalRequest request) {
        return ResponseEntity.ok(proposalService.createProposal(request));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    public ResponseEntity<List<ProposalResponse>> getAllProposals() {
        return ResponseEntity.ok(proposalService.getAllProposals());
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<ProposalResponse>> getMyProposals() {
        return ResponseEntity.ok(proposalService.getMyProposals());
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    public ResponseEntity<List<ProposalResponse>> getProposalsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(proposalService.getProposalsByStatus(status));
    }
    
    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ADMIN')")
    public ResponseEntity<ProposalResponse> reviewProposal(
            @PathVariable Long id,
            @Valid @RequestBody ProposalReviewRequest request) {
        return ResponseEntity.ok(proposalService.reviewProposal(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposal(@PathVariable Long id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.noContent().build();
    }
}
