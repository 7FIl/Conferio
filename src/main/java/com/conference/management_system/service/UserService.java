package com.conference.management_system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conference.management_system.dto.UserResponse;
import com.conference.management_system.entity.User;
import com.conference.management_system.entity.User.Role;
import com.conference.management_system.exception.ApiException;
import com.conference.management_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound("User not found"));
        return UserResponse.fromUser(user);
    }
    
    @Transactional
    public UserResponse updateUserRole(Long id, String roleStr) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound("User not found"));
        
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            user.setRole(role);
            userRepository.save(user);
            return UserResponse.fromUser(user);
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Invalid role: " + roleStr);
        }
    }
    
    @Transactional
    public String deleteUser(Long id) {
        User userToDelete = userRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound("User not found"));

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw ApiException.unauthorized("User not authenticated");
        }
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> ApiException.notFound("Current user not found"));

        // If the user to delete is an admin and is the same as the current user
        if (userToDelete.getRole() == Role.ADMIN && userToDelete.getId().equals(currentUser.getId())) {
            long adminCount = userRepository.findAll()
                    .stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .count();
            if (adminCount <= 1) {
                throw ApiException.forbidden("At least one administrator must remain active.");
            }
        }

        userRepository.deleteById(id);
        return userToDelete.getUsername();
    }
}
