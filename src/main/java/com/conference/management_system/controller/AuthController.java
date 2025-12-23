package com.conference.management_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.management_system.dto.AuthResponse;
import com.conference.management_system.dto.LoginRequest;
import com.conference.management_system.dto.RegisterRequest;
import com.conference.management_system.dto.UserResponse;
import com.conference.management_system.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setTokenCookie(response, authResponse.getToken());
        authResponse.setToken(null); // Don't send token in response body
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setTokenCookie(response, authResponse.getToken());
        // Keep token in response for JavaScript clients that need it
        // Cookie is HttpOnly for security, but also provide token in response for SPA usage
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidate user session and clear authentication token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<String> logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", "token=; Max-Age=0; HttpOnly; Path=/; SameSite=Strict");
        return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user", description = "Returns profile of the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile returned"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(authService.currentUser());
    }
    
    private void setTokenCookie(HttpServletResponse response, String token) {
        String cookieValue = String.format(
            "token=%s; Max-Age=86400; HttpOnly; Path=/; SameSite=Strict; Secure",
            token
        );
        response.addHeader("Set-Cookie", cookieValue);
    }
}
