package com.fiqhmaster.controller;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.service.ScheduledExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduled-exams")
@RequiredArgsConstructor
public class ScheduledExamController {
    
    private final ScheduledExamService scheduledExamService;
    
    @PostMapping("/create")
    public ResponseEntity<ScheduledExamDTO> createExam(
            @RequestBody CreateScheduledExamRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ScheduledExamDTO exam = scheduledExamService.createScheduledExam(userId, request);
        return ResponseEntity.ok(exam);
    }
    
    @GetMapping("/code/{examCode}")
    public ResponseEntity<ScheduledExamDTO> getExamByCode(
            @PathVariable String examCode,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ScheduledExamDTO exam = scheduledExamService.getExamByCode(examCode, userId);
        return ResponseEntity.ok(exam);
    }
    
    @PostMapping("/join")
    public ResponseEntity<ExamDetailsDTO> joinExam(
            @RequestBody JoinExamRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ExamDetailsDTO details = scheduledExamService.joinExam(userId, request.getExamCode());
        return ResponseEntity.ok(details);
    }
    
    @GetMapping("/{examId}/details")
    public ResponseEntity<ExamDetailsDTO> getExamDetails(
            @PathVariable Long examId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ExamDetailsDTO details = scheduledExamService.getExamDetails(examId, userId);
        return ResponseEntity.ok(details);
    }
    
    @PostMapping("/{examId}/start")
    public ResponseEntity<Map<String, String>> startExam(
            @PathVariable Long examId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        scheduledExamService.startExam(userId, examId);
        return ResponseEntity.ok(Map.of("message", "بدأ الاختبار بنجاح"));
    }
    
    @GetMapping("/my-exams")
    public ResponseEntity<List<ScheduledExamDTO>> getMyExams(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<ScheduledExamDTO> exams = scheduledExamService.getUserExams(userId);
        return ResponseEntity.ok(exams);
    }
    
    @GetMapping("/created")
    public ResponseEntity<List<ScheduledExamDTO>> getCreatedExams(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<ScheduledExamDTO> exams = scheduledExamService.getCreatedExams(userId);
        return ResponseEntity.ok(exams);
    }
    
    @DeleteMapping("/{examId}/cancel")
    public ResponseEntity<Map<String, String>> cancelExam(
            @PathVariable Long examId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        scheduledExamService.cancelExam(userId, examId);
        return ResponseEntity.ok(Map.of("message", "تم إلغاء الاختبار"));
    }


@GetMapping("/{examId}/questions")
public ResponseEntity<List<QuestionDTO>> getExamQuestions(
        @PathVariable Long examId,
        Authentication authentication) {
    Long userId = (Long) authentication.getPrincipal();
    List<QuestionDTO> questions = scheduledExamService.getExamQuestions(userId, examId);
    return ResponseEntity.ok(questions);
}

}