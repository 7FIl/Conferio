package com.conference.management_system.dto;

import java.time.LocalDateTime;

import com.conference.management_system.entity.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private Long proposalId;
    private Long speakerId;
    private String speakerName;
    private String title;
    private String description;
    private LocalDateTime sessionTime;
    private Integer durationMinutes;
    private String room;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Session.SessionStatus status;
    private LocalDateTime createdAt;
}
