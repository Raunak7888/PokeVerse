package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.QuizAttempt;
import com.pokequiz.quiz.model.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findBySessionId(QuizSession sessionId);

    QuizAttempt findBySessionIdAndStartTime(QuizSession sessionId, LocalDateTime startTime);

    List<QuizAttempt> findBySessionIdAndStartTimeBetween(QuizSession sessionId, LocalDateTime startTime, LocalDateTime endTime);

    List<QuizAttempt> findByQuestionId(Long questionId);
}
