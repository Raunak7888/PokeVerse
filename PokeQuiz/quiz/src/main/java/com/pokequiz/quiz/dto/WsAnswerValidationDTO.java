package com.pokequiz.quiz.dto;

import lombok.*;

@Getter
@Setter
public class WsAnswerValidationDTO {
    private Long userId;
    private Long roomId;
    private Long questionId;
    private String answer;
    private boolean correct;
}
