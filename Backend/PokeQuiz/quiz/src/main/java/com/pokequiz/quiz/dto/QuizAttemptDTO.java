package com.pokequiz.quiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuizAttemptDTO {
    private Long sessionId;
    private Long questionId;
    private String selectedAnswer;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
