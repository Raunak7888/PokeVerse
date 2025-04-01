package com.pokequiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerAnswerDto {
    private Long playerId;
    private Long quizId;
    private String answer;
    private boolean correct;
}

