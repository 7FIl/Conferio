package com.conference.management_system.dto;

import java.time.LocalDateTime;

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
public class SessionRequest {
    
    @NotNull(message = "Proposal ID is required")
    private Long proposalId;
    
    @NotBlank(message = "Room is required")
    private String room;
    
    @NotNull(message = "Session time is required")
    @Future(message = "Session time must be in the future")
    private LocalDateTime sessionTime;
    
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    private Integer durationMinutes;
    
    @Min(value = 1, message = "Max participants must be at least 1")
    private Integer maxParticipants;
}
