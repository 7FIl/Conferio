package com.conference.management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to submit a new conference proposal")
public class ProposalRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Schema(description = "Proposal title", example = "AI and Machine Learning in Modern Systems", minLength = 5, maxLength = 200)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, message = "Description must be at least 20 characters")
    @Schema(description = "Detailed proposal description", 
            example = "This proposal discusses the latest advancements in AI and ML technologies...")
    private String description;
}
