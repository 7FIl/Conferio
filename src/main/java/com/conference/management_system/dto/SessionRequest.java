package com.conference.management_system.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new conference session")
public class SessionRequest {
    
    @NotNull(message = "Proposal ID is required")
    @Schema(description = "ID of the approved proposal", example = "1")
    private Long proposalId;
    
    @NotBlank(message = "Room is required")
    @Schema(description = "Session room or location", example = "Main Hall - Room A")
    private String room;
    
    @NotNull(message = "Session time is required")
    @Future(message = "Session time must be in the future")
    @Schema(description = "Session date and time (ISO 8601 format)", example = "2024-12-25T14:30:00")
    private LocalDateTime sessionTime;
    
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Schema(description = "Session duration in minutes", example = "90", minimum = "15")
    private Integer durationMinutes;
    
    @Min(value = 1, message = "Max participants must be at least 1")
    @Schema(description = "Maximum number of participants allowed", example = "100", minimum = "1")
    private Integer maxParticipants;
}
