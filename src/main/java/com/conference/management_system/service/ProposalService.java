package com.conference.management_system.service;

import com.conference.management_system.dto.ProposalRequest;
import com.conference.management_system.dto.ProposalResponse;
import com.conference.management_system.dto.ProposalReviewRequest;
import com.conference.management_system.entity.Proposal;
import com.conference.management_system.entity.Session;
import com.conference.management_system.entity.User;
import com.conference.management_system.repository.ProposalRepository;
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
public class ProposalService {
    
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    
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
        Proposal.ProposalStatus proposalStatus = Proposal.ProposalStatus.valueOf(status.toUpperCase());
        return proposalRepository.findByStatus(proposalStatus).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProposalResponse reviewProposal(Long proposalId, ProposalReviewRequest request) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        if (proposal.getStatus() != Proposal.ProposalStatus.PENDING) {
            throw new RuntimeException("Proposal already reviewed");
        }
        
        User reviewer = getCurrentUser();
        Proposal.ProposalStatus newStatus = Proposal.ProposalStatus.valueOf(request.getStatus().toUpperCase());
        
        proposal.setStatus(newStatus);
        proposal.setReviewedBy(reviewer);
        proposal.setReviewedAt(LocalDateTime.now());
        
        if (newStatus == Proposal.ProposalStatus.REJECTED) {
            proposal.setRejectionReason(request.getRejectionReason());
        } else if (newStatus == Proposal.ProposalStatus.ACCEPTED) {
            // Auto-create session from accepted proposal
            createSessionFromProposal(proposal);
        }
        
        Proposal updated = proposalRepository.save(proposal);
        return mapToResponse(updated);
    }
    
    private void createSessionFromProposal(Proposal proposal) {
        Session session = new Session();
        session.setProposal(proposal);
        session.setSpeaker(proposal.getUser());
        session.setTitle(proposal.getTitle());
        session.setDescription(proposal.getDescription());
        session.setStatus(Session.SessionStatus.SCHEDULED);
        // Session time and room should be set by coordinator later
        
        sessionRepository.save(session);
    }
    
    @Transactional
    public void deleteProposal(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        User currentUser = getCurrentUser();
        if (!proposal.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own proposals");
        }
        
        if (proposal.getStatus() != Proposal.ProposalStatus.PENDING) {
            throw new RuntimeException("Cannot delete reviewed proposal");
        }
        
        proposalRepository.delete(proposal);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private ProposalResponse mapToResponse(Proposal proposal) {
        ProposalResponse response = new ProposalResponse();
        response.setId(proposal.getId());
        response.setUserId(proposal.getUser().getId());
        response.setUsername(proposal.getUser().getUsername());
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
