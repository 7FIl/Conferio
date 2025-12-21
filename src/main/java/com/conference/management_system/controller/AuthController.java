package com.conference.management_system.controller;

import com.conference.management_system.dto.AuthResponse;
import com.conference.management_system.dto.LoginRequest;
import com.conference.management_system.dto.RegisterRequest;
import com.conference.management_system.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setTokenCookie(response, authResponse.getToken());
        authResponse.setToken(null); // Don't send token in response body
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setTokenCookie(response, authResponse.getToken());
        authResponse.setToken(null); // Don't send token in response body
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", "token=; Max-Age=0; HttpOnly; Path=/; SameSite=Strict");
        return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
    }
    
    private void setTokenCookie(HttpServletResponse response, String token) {
        String cookieValue = String.format(
            "token=%s; Max-Age=86400; HttpOnly; Path=/; SameSite=Strict; Secure",
            token
        );
        response.addHeader("Set-Cookie", cookieValue);
    }
}
