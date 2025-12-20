package com.conference.management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalReviewRequest {
    
    @NotBlank(message = "Status is required (ACCEPTED or REJECTED)")
    private String status;
    
    private String rejectionReason;
}
