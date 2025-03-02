package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(nativeQuery = true, value = """
    SELECT * FROM questions 
    WHERE (:region IS NULL OR region = :region) 
      AND (:difficulty IS NULL OR difficulty = :difficulty) 
      AND (:quizType IS NULL OR quiz_type = :quizType)
    ORDER BY RANDOM() 
    LIMIT :limit
""")
    List<Question> findRandomQuestionsAsPerDifficultyAndRegion(
            @Param("region") String region,
            @Param("difficulty") String difficulty,
            @Param("quizType") String quizType,
            @Param("limit") int limit
    );

    @Query(nativeQuery = true, value = """
    SELECT * FROM questions 
    WHERE (:region IS NULL OR region = :region) 
      AND (:difficulty IS NULL OR difficulty = :difficulty) 
      AND (:quizType IS NULL OR quiz_type = :quizType)
""")
    List<Question> findByDifficultyAndRegion(
            @Param("region") String region,
            @Param("difficulty") String difficulty,
            @Param("quizType") String quizType
    );



    @Query(nativeQuery = true, value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomQuestions(@Param("limit") int limit);

    List<Question> findByDifficulty(String difficulty);

    List<Question> findByRegion(String region);

    Optional<Question> findByQuestion(String question);

    List<Question> findByQuizType(String quizType);

    Optional<Question> findById(Long id);

    @Query("SELECT q FROM Question q WHERE q.difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    List<Question> findByDifficultyLimited(@Param("difficulty") String difficulty, @Param("limit") int limit);

    @Query(value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion();

    @Query("""
    SELECT q FROM Question q 
    WHERE (:excludedIds IS NULL OR q.id NOT IN :excludedIds) 
    ORDER BY RANDOM() 
    LIMIT 1
""")
    Question findRandomQuestionExcluding(@Param("excludedIds") List<Long> excludedIds);



}