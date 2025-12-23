package com.conference.management_system.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conference.management_system.dto.AuthResponse;
import com.conference.management_system.dto.LoginRequest;
import com.conference.management_system.dto.RegisterRequest;
import com.conference.management_system.dto.UserResponse;
import com.conference.management_system.entity.User;
import com.conference.management_system.exception.ApiException;
import com.conference.management_system.repository.UserRepository;
import com.conference.management_system.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());
        // Validasi username dan email sudah terdaftar
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username already exists: {}", request.getUsername());
            throw ApiException.conflict("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw ApiException.conflict("Email already exists");
        }
        
        // Buat user baru
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(User.Role.USER);
        
        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                "Registration successful"
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            log.info("Authentication successful for user: {}", request.getUsername());
            
            // Get user details
                User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> ApiException.notFound("User not found"));
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user);
            log.info("JWT token generated for user: {}", request.getUsername());
            
            return new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    "Login successful"
            );
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw ApiException.internalServerError("Login failed. Please try again.");
        }
    }

    /**
     * Return the currently authenticated user profile.
     * Throws RuntimeException to be handled by global exception handler if unauthenticated.
     */
    public UserResponse currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("currentUser called without authentication");
            throw ApiException.unauthorized("User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        return UserResponse.fromUser(user);
    }
}

