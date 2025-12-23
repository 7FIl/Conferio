package com.conference.management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login credentials")
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    @Schema(description = "Username or email address", example = "john.doe@example.com")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "SecurePassword123!")
    private String password;
}
