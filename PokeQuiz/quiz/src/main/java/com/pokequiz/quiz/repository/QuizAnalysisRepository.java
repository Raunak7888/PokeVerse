package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.QuizAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnalysisRepository extends JpaRepository<QuizAnalysis, Long> {
    Optional<QuizAnalysis> findBySessionId(Long sessionId);
    List<QuizAnalysis> findAllByUserId(Long userId);
    List<QuizAnalysis> findAllByQuizType(String quizType);
    List<QuizAnalysis> findAllByDifficulty(String difficulty);
    List<QuizAnalysis> findAllByRegion(String region);
    Optional<QuizAnalysis> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
