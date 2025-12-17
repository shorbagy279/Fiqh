package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.*;
import com.fiqhmaster.exception.ResourceNotFoundException;
import com.fiqhmaster.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledExamService {
    
    private final ScheduledExamRepository scheduledExamRepository;
    private final ExamParticipantRepository examParticipantRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    
    @Transactional
    public ScheduledExamDTO createScheduledExam(Long creatorId, CreateScheduledExamRequest request) {
        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        
        ScheduledExam exam = new ScheduledExam();
        exam.setCreator(creator);
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setMaxParticipants(request.getMaxParticipants());
        
        // Store question IDs
        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            exam.setQuestionIds(request.getQuestionIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
            exam.setTotalQuestions(request.getQuestionIds().size());
        }
        
        // Store category IDs
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            exam.setCategoryIds(request.getCategoryIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        }
        
        ScheduledExam saved = scheduledExamRepository.save(exam);
        log.info("Created scheduled exam with code: {}", saved.getExamCode());
        
        return toDTO(saved, creatorId);
    }
    
    @Transactional(readOnly = true)
    public ScheduledExamDTO getExamByCode(String examCode, Long userId) {
        ScheduledExam exam = scheduledExamRepository.findByExamCode(examCode)
            .orElseThrow(() -> new ResourceNotFoundException("كود الاختبار غير صحيح"));
        
        return toDTO(exam, userId);
    }
    
    @Transactional
    public ExamDetailsDTO joinExam(Long userId, String examCode) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        
        ScheduledExam exam = scheduledExamRepository.findByExamCode(examCode)
            .orElseThrow(() -> new ResourceNotFoundException("كود الاختبار غير صحيح"));
        
        if (!exam.getIsActive()) {
            throw new IllegalArgumentException("الاختبار غير نشط");
        }
        
        if (exam.isExpired()) {
            throw new IllegalArgumentException("انتهى وقت الاختبار");
        }
        
        if (exam.getMaxParticipants() != null && 
            exam.getCurrentParticipants() >= exam.getMaxParticipants()) {
            throw new IllegalArgumentException("الاختبار ممتلئ");
        }
        
        // Check if already registered
        if (examParticipantRepository.existsByExamIdAndUserId(exam.getId(), userId)) {
            throw new IllegalArgumentException("أنت مسجل بالفعل في هذا الاختبار");
        }
        
        // Register participant
        ExamParticipant participant = new ExamParticipant();
        participant.setExam(exam);
        participant.setUser(user);
        participant.setStatus("REGISTERED");
        examParticipantRepository.save(participant);
        
        // Update participant count
        exam.setCurrentParticipants(exam.getCurrentParticipants() + 1);
        scheduledExamRepository.save(exam);
        
        log.info("User {} joined exam {}", userId, examCode);
        
        return getExamDetails(exam.getId(), userId);
    }
    
    @Transactional(readOnly = true)
    public ExamDetailsDTO getExamDetails(Long examId, Long userId) {
        ScheduledExam exam = scheduledExamRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("الاختبار غير موجود"));
        
        List<ExamParticipant> participants = examParticipantRepository.findByExamId(examId);
        
        ExamParticipant userParticipant = examParticipantRepository
            .findByExamIdAndUserId(examId, userId)
            .orElse(null);
        
        ExamDetailsDTO details = new ExamDetailsDTO();
        details.setExam(toDTO(exam, userId));
        details.setParticipants(participants.stream()
            .map(this::toParticipantDTO)
            .collect(Collectors.toList()));
        details.setUserRegistered(userParticipant != null);
        details.setUserStatus(userParticipant != null ? userParticipant.getStatus() : null);
        
        return details;
    }
    
    @Transactional
    public void startExam(Long userId, Long examId) {
        ExamParticipant participant = examParticipantRepository
            .findByExamIdAndUserId(examId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("أنت غير مسجل في هذا الاختبار"));
        
        ScheduledExam exam = participant.getExam();
        
        if (!exam.isStarted()) {
            throw new IllegalArgumentException("لم يبدأ الاختبار بعد");
        }
        
        if (exam.isExpired()) {
            throw new IllegalArgumentException("انتهى وقت الاختبار");
        }
        
        if ("STARTED".equals(participant.getStatus()) || "COMPLETED".equals(participant.getStatus())) {
            throw new IllegalArgumentException("لقد بدأت الاختبار بالفعل");
        }
        
        participant.setStatus("STARTED");
        participant.setStartedAt(LocalDateTime.now());
        examParticipantRepository.save(participant);
        
        log.info("User {} started exam {}", userId, examId);
    }
    
    @Transactional(readOnly = true)
    public List<ScheduledExamDTO> getUserExams(Long userId) {
        List<ExamParticipant> participants = examParticipantRepository.findByUserId(userId);
        
        return participants.stream()
            .map(p -> toDTO(p.getExam(), userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ScheduledExamDTO> getCreatedExams(Long creatorId) {
        return scheduledExamRepository.findByCreatorIdOrderByCreatedAtDesc(creatorId)
            .stream()
            .map(exam -> toDTO(exam, creatorId))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelExam(Long userId, Long examId) {
        ScheduledExam exam = scheduledExamRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("الاختبار غير موجود"));
        
        if (!exam.getCreator().getId().equals(userId)) {
            throw new IllegalArgumentException("ليس لديك صلاحية لإلغاء هذا الاختبار");
        }
        
        exam.setIsActive(false);
        scheduledExamRepository.save(exam);
        
        log.info("Exam {} cancelled by creator {}", examId, userId);
    }
    
    private ScheduledExamDTO toDTO(ScheduledExam exam, Long userId) {
        ScheduledExamDTO dto = new ScheduledExamDTO();
        dto.setId(exam.getId());
        dto.setExamCode(exam.getExamCode());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setStartTime(exam.getStartTime());
        dto.setDurationMinutes(exam.getDurationMinutes());
        dto.setTotalQuestions(exam.getTotalQuestions());
        dto.setIsActive(exam.getIsActive());
        dto.setMaxParticipants(exam.getMaxParticipants());
        dto.setCurrentParticipants(exam.getCurrentParticipants());
        dto.setIsStarted(exam.isStarted());
        dto.setIsExpired(exam.isExpired());
        dto.setCanJoin(exam.canJoin());
        dto.setCreatorName(exam.getCreator().getFullName());
        dto.setCreatedAt(exam.getCreatedAt());
        
        boolean isRegistered = examParticipantRepository
            .existsByExamIdAndUserId(exam.getId(), userId);
        dto.setIsRegistered(isRegistered);
        
        return dto;
    }
    
    private ExamParticipantDTO toParticipantDTO(ExamParticipant participant) {
        ExamParticipantDTO dto = new ExamParticipantDTO();
        dto.setId(participant.getId());
        dto.setUserId(participant.getUser().getId());
        dto.setUserName(participant.getUser().getFullName());
        dto.setStatus(participant.getStatus());
        dto.setScore(participant.getScore());
        dto.setJoinedAt(participant.getJoinedAt());
        dto.setStartedAt(participant.getStartedAt());
        dto.setCompletedAt(participant.getCompletedAt());
        return dto;
    }

    @Transactional(readOnly = true)
public List<QuestionDTO> getExamQuestions(Long userId, Long examId) {
    // Verify user is registered for this exam
    ExamParticipant participant = examParticipantRepository
        .findByExamIdAndUserId(examId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("أنت غير مسجل في هذا الاختبار"));
    
    ScheduledExam exam = participant.getExam();
    
    if (exam.getQuestionIds() == null || exam.getQuestionIds().isEmpty()) {
        throw new IllegalArgumentException("لا توجد أسئلة في هذا الاختبار");
    }
    
    // Parse question IDs
    List<Long> questionIds = Arrays.stream(exam.getQuestionIds().split(","))
        .map(Long::parseLong)
        .collect(Collectors.toList());
    
    // Fetch questions
    List<Question> questions = questionRepository.findAllById(questionIds);
    
    return questions.stream()
        .map(q -> toQuestionDTO(q, userId))
        .collect(Collectors.toList());
}

private QuestionDTO toQuestionDTO(Question question, Long userId) {
    QuestionDTO dto = new QuestionDTO();
    dto.setId(question.getId());
    dto.setCategoryId(question.getCategory().getId());
    dto.setCategoryName(question.getCategory().getNameAr());
    dto.setQuestionAr(question.getQuestionAr());
    dto.setQuestionEn(question.getQuestionEn());
    dto.setDifficulty(question.getDifficulty());

    dto.setOptionsAr(Arrays.asList(
        question.getOptionAAr(),
        question.getOptionBAr(),
        question.getOptionCAr(),
        question.getOptionDAr()
    ));

    dto.setOptionsEn(Arrays.asList(
        question.getOptionAEn(),
        question.getOptionBEn(),
        question.getOptionCEn(),
        question.getOptionDEn()
    ));

    return dto;
}
}