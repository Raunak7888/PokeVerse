package com.pokequiz.quiz.service;

import com.pokequiz.quiz.dto.QuizAttemptDTO;
import com.pokequiz.quiz.model.Answer;
import com.pokequiz.quiz.model.Question;
import com.pokequiz.quiz.model.QuizAttempt;
import com.pokequiz.quiz.model.QuizSession;
import com.pokequiz.quiz.repository.AnswerRepository;
import com.pokequiz.quiz.repository.QuestionRepository;
import com.pokequiz.quiz.repository.QuizAttemptRepository;
import com.pokequiz.quiz.repository.QuizSessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, QuizSessionRepository quizSessionRepository,
                              QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizSessionRepository = quizSessionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public void saveQuizAttempt(QuizSession session, Long questionId, String selectedAnswer, boolean isCorrect,
                                LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            INSERT INTO quiz_attempts 
            (session_id, question_id, selected_answer, is_correct, start_time, end_time, created_at) 
            VALUES (:sessionId, :questionId, :selectedAnswer, :isCorrect, :startTime, :endTime, NOW())
        """;

        entityManager.createNativeQuery(sql)
                .setParameter("sessionId", session.getSessionId())
                .setParameter("questionId", questionId)
                .setParameter("selectedAnswer", selectedAnswer)
                .setParameter("isCorrect", isCorrect)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .executeUpdate();
    }

    public List<QuizAttempt> getAttemptsBySession(QuizSession session) {
        return quizAttemptRepository.findBySessionId(session);
    }

    public QuizAttempt evaluateQuizAttempt(QuizAttemptDTO dto) {
        Optional<Question> existingQuestion = questionRepository.findById(dto.getQuestionId());
        boolean isCorrect = false;

        if (existingQuestion.isPresent()) {
            Answer existingAnswer = answerRepository.findByQuestion(existingQuestion.get());
            if (existingAnswer != null && Objects.equals(existingAnswer.getCorrectAnswer(), dto.getSelectedAnswer())) {
                isCorrect = true;
            }
        }

        // Fetch the active session for the user
        QuizSession session = quizSessionRepository.findBySessionIdAndStatus(dto.getSessionId(), QuizSession.SessionStatus.IN_PROGRESS);
        if (session == null) {
            throw new IllegalStateException("No active quiz session found for session ID: " + dto.getSessionId());
        }

        // Check if the attempt already exists
        QuizAttempt isAlreadyPresent = quizAttemptRepository.findBySessionIdAndStartTime(session, dto.getStartTime());
        if (isAlreadyPresent != null) {
            return isAlreadyPresent;
        }

        // Save the attempt
        saveQuizAttempt(session, dto.getQuestionId(), dto.getSelectedAnswer(), isCorrect, dto.getStartTime(), dto.getEndTime());
        return quizAttemptRepository.findBySessionIdAndStartTime(session, dto.getStartTime());
    }

    public List<QuizAttempt> getAttemptsBySessionAndTime(QuizSession session, LocalDateTime startTime, LocalDateTime endTime) {
        return quizAttemptRepository.findBySessionIdAndStartTimeBetween(session, startTime, endTime);
    }

    public List<QuizAttempt> getAttemptsByQuestionId(Long questionId) {
        return quizAttemptRepository.findByQuestionId(questionId);
    }
}
