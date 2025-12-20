package com.conference.management_system.dto;

import java.time.LocalDateTime;

import com.conference.management_system.entity.Proposal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalResponse {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private Proposal.ProposalStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String rejectionReason;
}
