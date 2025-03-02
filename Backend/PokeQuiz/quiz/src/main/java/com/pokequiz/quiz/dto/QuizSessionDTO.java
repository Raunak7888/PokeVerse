package com.pokequiz.quiz.dto;

import com.pokequiz.quiz.model.QuizSession.SessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuizSessionDTO {
    private Long userId;           // User identifier
    private String difficulty;       // Difficulty level
    private String region;           // Region of the quiz
    private String quizType;         // Type of quiz (e.g., trivia, battle, etc.)
    private int totalQuestions;      // Total number of questions
    private LocalDateTime startTime; // Session start time
    private LocalDateTime endTime;   // Session end time (nullable if in progress)
    private SessionStatus status;    // IN_PROGRESS, COMPLETED, ABANDONED
}
