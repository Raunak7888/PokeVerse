package com.pokequiz.quiz.service;

import com.pokequiz.quiz.dto.QuizSessionDTO;
import com.pokequiz.quiz.model.QuizSession;
import com.pokequiz.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;

    public QuizSessionService(QuizSessionRepository quizSessionRepository) {
        this.quizSessionRepository = quizSessionRepository;
    }

    // 🎯 Create a new quiz session
    public QuizSession createSession(QuizSessionDTO dto) {
        QuizSession newSession = QuizSession.builder()
                .userId(dto.getUserId())
                .difficulty(dto.getDifficulty())
                .region(dto.getRegion())
                .quizType(dto.getQuizType())
                .totalQuestions(dto.getTotalQuestions())
                .status(QuizSession.SessionStatus.IN_PROGRESS)
                .build();

        return quizSessionRepository.save(newSession);
    }

    // 🔄 Update quiz session (endTime + status)
    public QuizSession updateSession(Long sessionId, QuizSession.SessionStatus status) {
        Optional<QuizSession> sessionOptional = quizSessionRepository.findById(sessionId);
        if (sessionOptional.isPresent()) {
            QuizSession session = sessionOptional.get();
            session.setStatus(status);
            if (status == QuizSession.SessionStatus.COMPLETED || status == QuizSession.SessionStatus.ABANDONED) {
                session.setEndTime(LocalDateTime.now());
            }
            return quizSessionRepository.save(session);
        }
        return null; // Session not found
    }

    // 📊 Get all quiz sessions
    public List<QuizSession> getAllSessions() {
        return quizSessionRepository.findAll();
    }

    // 🔍 Get quiz session by ID
    public QuizSession getSessionById(Long sessionId) {
        return quizSessionRepository.findById(sessionId).orElse(null);
    }

    // 📌 Find active session for a user (IN_PROGRESS status only)
    public QuizSession findActiveSessionByUserId(Long userId) {
        return quizSessionRepository.findByUserIdAndStatus(userId, QuizSession.SessionStatus.IN_PROGRESS);
    }
}
