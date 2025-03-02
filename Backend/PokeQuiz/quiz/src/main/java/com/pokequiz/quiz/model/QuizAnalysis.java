package com.pokequiz.quiz.model;

import com.pokequiz.quiz.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Ensure one analysis per session
    private Long sessionId;

    @Column(nullable = false)
    private Long userId; // User who completed the quiz

    @Column(nullable = false)
    private String quizType; // "TRIVIA", "PUZZLE", etc.

    @Column(nullable = false)
    private String difficulty; // "EASY", "MEDIUM", "HARD"

    @Column(nullable = false)
    private String region; // "KANTO", "JOHTO", etc.

    @Column(nullable = false)
    private int totalQuestions; // Total number of questions

    @Column(nullable = false)
    private int correctAnswers; // Correct answers count

    @Column(nullable = false)
    private int wrongAnswers; // Incorrect answers count

    @Column(nullable = false)
    private double accuracy; // Accuracy in percentage (e.g., 85.5)

    @Column(nullable = false)
    private Long totalDuration; // Total time in milliseconds

    @Column(nullable = false)
    private Long averageTimePerQuestion; // Avg time per question in ms

    @Column(nullable = false)
    private Long fastestAnswerTime; // Fastest answer time in ms

    @Column(nullable = false)
    private Long slowestAnswerTime; // Slowest answer time in ms

    @Column(nullable = false)
    private String answerSpeedRating; // "Fast", "Average", "Slow"

    @Column(nullable = false)
    private String performanceRating; // "Ace", "Pro", "Rookie"

    @CreationTimestamp
    private LocalDateTime createdAt; // Auto-generated timestamp

    @ElementCollection
    @CollectionTable(name = "question_analysis", joinColumns = @JoinColumn(name = "quiz_analysis_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "questionId", column = @Column(name = "question_id")),
            @AttributeOverride(name = "question", column = @Column(name = "question_text")),
            @AttributeOverride(name = "difficulty", column = @Column(name = "question_difficulty")),
            @AttributeOverride(name = "region", column = @Column(name = "question_region")),
            @AttributeOverride(name = "quizType", column = @Column(name = "question_quiz_type")),
            @AttributeOverride(name = "selectedAnswer", column = @Column(name = "selected_answer")),
            @AttributeOverride(name = "correctAnswer", column = @Column(name = "correct_answer")),
            @AttributeOverride(name = "isCorrect", column = @Column(name = "is_correct")),
            @AttributeOverride(name = "timeTaken", column = @Column(name = "time_taken"))
    })
    private List<QuestionAnalysis> questionAnalysis;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnalysis {

        private Long questionId; // Unique identifier for the question

        private String question; // Actual question text

        private String difficulty; // Question-specific difficulty

        private String region; // Related Pokémon region

        private String quizType; // Type of quiz (Trivia, Puzzle, etc.)

        private String selectedAnswer; // User's selected answer

        @Convert(converter = StringListConverter.class)
        private List<String> options; // Available options for this question

        private String correctAnswer; // The right answer

        private boolean isCorrect; // Whether the answer is correct

        private Long timeTaken; // Time taken in milliseconds
    }
}
