package com.conference.management_system.dto;

import java.time.LocalDateTime;

import com.conference.management_system.entity.Registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long sessionId;
    private String sessionTitle;
    private LocalDateTime sessionTime;
    private Registration.RegistrationStatus status;
    private LocalDateTime registeredAt;
}
