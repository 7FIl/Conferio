package com.conference.management_system.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long sessionId;
    private String sessionTitle;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
