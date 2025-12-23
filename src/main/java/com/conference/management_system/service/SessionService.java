package com.conference.management_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conference.management_system.dto.SessionRequest;
import com.conference.management_system.dto.SessionResponse;
import com.conference.management_system.entity.Proposal;
import com.conference.management_system.entity.Session;
import com.conference.management_system.entity.User;
import com.conference.management_system.exception.ApiException;
import com.conference.management_system.repository.ProposalRepository;
import com.conference.management_system.repository.SessionRepository;
import com.conference.management_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public SessionResponse createSession(SessionRequest request) {
        // Validate proposal exists and is accepted
        Proposal proposal = proposalRepository.findById(request.getProposalId())
                .orElseThrow(() -> ApiException.notFound("Proposal not found"));
        
        if (proposal.getStatus() != Proposal.ProposalStatus.ACCEPTED) {
            throw ApiException.badRequest("Only accepted proposals can be scheduled");
        }
        
        // Check for time conflicts
        LocalDateTime endTime = request.getSessionTime().plusMinutes(request.getDurationMinutes());
        List<Session> conflicts = sessionRepository.findConflictingSessions(
                request.getSessionTime(), endTime);
        
        if (!conflicts.isEmpty()) {
            throw ApiException.conflict("Time slot conflicts with existing session");
        }
        
        Session session = new Session();
        session.setProposal(proposal);
        session.setSpeaker(proposal.getUser());
        session.setTitle(proposal.getTitle());
        session.setDescription(proposal.getDescription());
        session.setSessionTime(request.getSessionTime());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setRoom(request.getRoom());
        
        if (request.getMaxParticipants() != null) {
            session.setMaxParticipants(request.getMaxParticipants());
        }
        
        Session saved = sessionRepository.save(session);
        return mapToResponse(saved);
    }
    
    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SessionResponse> getUpcomingSessions() {
        return sessionRepository.findUpcomingSessions(LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public SessionResponse getSessionById(Long id) {
        Session session = sessionRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound("Session not found"));
        return mapToResponse(session);
    }
    
    public List<SessionResponse> getMySessions() {
        User currentUser = getCurrentUser();
        return sessionRepository.findBySpeakerId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SessionResponse updateSession(Long id, SessionRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Session not found"));
        
        // Check for time conflicts (excluding current session)
        LocalDateTime endTime = request.getSessionTime().plusMinutes(request.getDurationMinutes());
        List<Session> conflicts = sessionRepository.findConflictingSessions(
                request.getSessionTime(), endTime).stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());
        
        if (!conflicts.isEmpty()) {
            throw ApiException.conflict("Time slot conflicts with existing session");
        }
        
        session.setSessionTime(request.getSessionTime());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setRoom(request.getRoom());
        
        if (request.getMaxParticipants() != null) {
            session.setMaxParticipants(request.getMaxParticipants());
        }
        
        Session updated = sessionRepository.save(session);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Session does not exist"));
        
        if (session.getCurrentParticipants() > 0) {
            throw ApiException.conflict("Cannot delete session with registered participants");
        }
        
        sessionRepository.delete(session);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> ApiException.notFound("User not found"));
    }
    
    private SessionResponse mapToResponse(Session session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setProposalId(session.getProposal().getId());
        response.setSpeakerId(session.getSpeaker().getId());
        response.setSpeakerName(session.getSpeaker().getFullName());
        response.setTitle(session.getTitle());
        response.setDescription(session.getDescription());
        response.setSessionTime(session.getSessionTime());
        response.setDurationMinutes(session.getDurationMinutes());
        response.setRoom(session.getRoom());
        response.setMaxParticipants(session.getMaxParticipants());
        response.setCurrentParticipants(session.getCurrentParticipants());
        response.setStatus(session.getStatus());
        response.setCreatedAt(session.getCreatedAt());
        return response;
    }
}
