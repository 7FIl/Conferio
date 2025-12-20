package com.conference.management_system.repository;

import com.conference.management_system.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySessionId(Long sessionId);
    List<Feedback> findByUserId(Long userId);
    Optional<Feedback> findByUserIdAndSessionId(Long userId, Long sessionId);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.session.id = :sessionId")
    Double getAverageRatingBySessionId(@Param("sessionId") Long sessionId);
}
