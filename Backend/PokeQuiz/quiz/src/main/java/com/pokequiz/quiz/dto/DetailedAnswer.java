package com.pokequiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedAnswer {
    private Long questionId;
    private String correctAnswer;
    private String userAnswer;
    private boolean isCorrect;
}
