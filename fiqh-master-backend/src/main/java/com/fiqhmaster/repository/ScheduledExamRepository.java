package com.fiqhmaster.repository;

import com.fiqhmaster.entity.ScheduledExam;
import com.fiqhmaster.entity.ExamParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledExamRepository extends JpaRepository<ScheduledExam, Long> {
    Optional<ScheduledExam> findByExamCode(String examCode);
    List<ScheduledExam> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
    
    @Query("SELECT e FROM ScheduledExam e WHERE e.isActive = true AND e.startTime > :now")
    List<ScheduledExam> findUpcomingExams(LocalDateTime now);
}

