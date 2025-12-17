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
public interface ExamParticipantRepository extends JpaRepository<ExamParticipant, Long> {
    Optional<ExamParticipant> findByExamIdAndUserId(Long examId, Long userId);
    List<ExamParticipant> findByExamId(Long examId);
    List<ExamParticipant> findByUserId(Long userId);
    Boolean existsByExamIdAndUserId(Long examId, Long userId);
    Long countByExamId(Long examId);
}