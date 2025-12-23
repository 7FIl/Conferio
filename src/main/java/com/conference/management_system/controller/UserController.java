package com.conference.management_system.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.management_system.dto.UserResponse;
import com.conference.management_system.dto.UserRoleRequest;
import com.conference.management_system.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "APIs for user management (Admin only)")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve list of all registered users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users returned"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve details of a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details returned"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "Change the role of a user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User role updated successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleRequest request) {
        return ResponseEntity.ok(userService.updateUserRole(id, request.getRole()));
    }
    
    public static class DeleteUserResponse {
        public LocalDateTime time;
        public String user;
        public String message;
        public DeleteUserResponse(LocalDateTime time, String user, String message) {
            this.time = time;
            this.user = user;
            this.message = message;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user", description = "Remove a user from the system (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable Long id) {
        String deletedUsername = userService.deleteUser(id);
        DeleteUserResponse response = new DeleteUserResponse(
            LocalDateTime.now(),
            deletedUsername,
            "User successfully deleted"
        );
        return ResponseEntity.ok(response);
    }
}
