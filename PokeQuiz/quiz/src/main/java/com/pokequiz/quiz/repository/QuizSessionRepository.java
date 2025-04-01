package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    QuizSession findByUserIdAndStatus(Long userId, QuizSession.SessionStatus status);
    QuizSession findBySessionIdAndStatus(Long sessionId, QuizSession.SessionStatus status);
}
