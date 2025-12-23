package com.conference.management_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conference.management_system.dto.ProposalRequest;
import com.conference.management_system.dto.ProposalResponse;
import com.conference.management_system.dto.ProposalReviewRequest;
import com.conference.management_system.entity.Proposal;
import com.conference.management_system.entity.User;
import com.conference.management_system.exception.ApiException;
import com.conference.management_system.repository.ProposalRepository;
import com.conference.management_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProposalService {
    
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ProposalResponse createProposal(ProposalRequest request) {
        User currentUser = getCurrentUser();
        
        Proposal proposal = new Proposal();
        proposal.setUser(currentUser);
        proposal.setTitle(request.getTitle());
        proposal.setDescription(request.getDescription());
        proposal.setStatus(Proposal.ProposalStatus.PENDING);
        
        Proposal saved = proposalRepository.save(proposal);
        return mapToResponse(saved);
    }
    
    public List<ProposalResponse> getAllProposals() {
        return proposalRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ProposalResponse> getMyProposals() {
        User currentUser = getCurrentUser();
        return proposalRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ProposalResponse> getProposalsByStatus(String status) {
        Proposal.ProposalStatus proposalStatus;
        try {
            proposalStatus = Proposal.ProposalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("Unknown proposal status: " + status);
        }
        return proposalRepository.findByStatus(proposalStatus).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProposalResponse reviewProposal(Long proposalId, ProposalReviewRequest request) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> ApiException.notFound("Proposal not found"));
        
        if (proposal.getStatus() != Proposal.ProposalStatus.PENDING) {
            throw ApiException.conflict("Proposal already reviewed");
        }
        
        User reviewer = getCurrentUser();
        Proposal.ProposalStatus newStatus;
        try {
            newStatus = Proposal.ProposalStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("Unknown review status: " + request.getStatus());
        }
        
        proposal.setStatus(newStatus);
        proposal.setReviewedBy(reviewer);
        proposal.setReviewedAt(LocalDateTime.now());
        
        if (newStatus == Proposal.ProposalStatus.REJECTED) {
            proposal.setRejectionReason(request.getRejectionReason());
        }
        // Note: Session creation is now done separately by coordinators
        
        Proposal updated = proposalRepository.save(proposal);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteProposal(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> ApiException.notFound("Proposal not found"));
        
        User currentUser = getCurrentUser();
        if (!proposal.getUser().getId().equals(currentUser.getId())) {
            throw ApiException.forbidden("You can only delete your own proposals");
        }
        
        if (proposal.getStatus() != Proposal.ProposalStatus.PENDING) {
            throw ApiException.conflict("Cannot delete reviewed proposal");
        }
        
        proposalRepository.delete(proposal);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> ApiException.notFound("User not found"));
    }
    
    private ProposalResponse mapToResponse(Proposal proposal) {
        ProposalResponse response = new ProposalResponse();
        response.setId(proposal.getId());
        response.setUserId(proposal.getUser().getId());
        response.setUsername(proposal.getUser().getUsername());
        response.setSubmitterName(proposal.getUser().getUsername());  // Set submitterName for UI
        response.setTitle(proposal.getTitle());
        response.setDescription(proposal.getDescription());
        response.setStatus(proposal.getStatus());
        response.setSubmittedAt(proposal.getSubmittedAt());
        response.setReviewedAt(proposal.getReviewedAt());
        if (proposal.getReviewedBy() != null) {
            response.setReviewedBy(proposal.getReviewedBy().getUsername());
        }
        response.setRejectionReason(proposal.getRejectionReason());
        return response;
    }
}
