package com.pokequiz.quiz.controller;

import com.pokequiz.quiz.dto.QuizAttemptDTO;
import com.pokequiz.quiz.model.QuizAttempt;
import com.pokequiz.quiz.model.QuizSession;
import com.pokequiz.quiz.repository.QuizSessionRepository;
import com.pokequiz.quiz.service.QuizAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final QuizSessionRepository quizSessionRepository;

    public QuizAttemptController(QuizAttemptService quizAttemptService, QuizSessionRepository quizSessionRepository) {
        this.quizAttemptService = quizAttemptService;
        this.quizSessionRepository = quizSessionRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuizAttempt(@RequestBody QuizAttemptDTO dto) {
        if (dto.getSessionId() == null || dto.getQuestionId() == null || dto.getSelectedAnswer() == null ||
                dto.getStartTime() == null || dto.getEndTime() == null) {
            return ResponseEntity.badRequest().body("Invalid request body");
        }

        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            return ResponseEntity.badRequest().body("Invalid start and end time");
        }

        QuizAttempt result = quizAttemptService.evaluateQuizAttempt(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/session")
    public ResponseEntity<List<QuizAttempt>> getAttemptsBySession(@RequestParam Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session ID: " + sessionId));

        List<QuizAttempt> attempts = quizAttemptService.getAttemptsBySession(session);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/session/time")
    public ResponseEntity<?> getAttemptsBySessionAndTime(@RequestParam Long sessionId,
                                                                         @RequestParam LocalDateTime startTime,
                                                                         @RequestParam LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            return ResponseEntity.badRequest().body("Invalid start and end time");
        }
        QuizSession session;
        try {
            session = quizSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid session ID: " + sessionId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid session ID: " + sessionId);
        }


        List<QuizAttempt> attempts = quizAttemptService.getAttemptsBySessionAndTime(session, startTime, endTime);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/question")
    public ResponseEntity<List<QuizAttempt>> getAttemptsByQuestionId(@RequestParam Long questionId) {
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByQuestionId(questionId);
        return ResponseEntity.ok(attempts);
    }
}
