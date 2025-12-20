package com.conference.management_system.repository;

import com.conference.management_system.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findBySpeakerId(Long speakerId);
    List<Session> findByStatus(Session.SessionStatus status);
    
    @Query("SELECT s FROM Session s WHERE s.sessionTime >= :now AND s.status = 'SCHEDULED' ORDER BY s.sessionTime")
    List<Session> findUpcomingSessions(@Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM Session s WHERE " +
           "s.sessionTime < :endTime AND " +
           "FUNCTION('TIMESTAMPADD', MINUTE, s.durationMinutes, s.sessionTime) > :startTime")
    List<Session> findConflictingSessions(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
