package com.conference.management_system.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.conference.management_system.entity.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUserId(Long userId);
    List<Registration> findBySessionId(Long sessionId);
    Optional<Registration> findByUserIdAndSessionId(Long userId, Long sessionId);
    boolean existsByUserIdAndSessionId(Long userId, Long sessionId);
    
    @Query("SELECT r FROM Registration r JOIN r.session s WHERE r.user.id = :userId " +
           "AND s.sessionTime < :endTime " +
           "AND FUNCTION('TIMESTAMPADD', MINUTE, s.durationMinutes, s.sessionTime) > :startTime " +
           "AND r.status = 'CONFIRMED'")
    List<Registration> findUserRegistrationConflicts(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
