package com.pokequiz.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class QuestionDTO {
    private Long id;

    @NotNull(message = "Question cannot be null")
    private String question;

    @NotNull(message = "difficulty cannot be null")
    private String difficulty;

    @NotNull(message = "region cannot be null")
    private String region;

    @NotNull(message = "quizType cannot be null")
    private String quizType;

    @NotNull(message = "options cannot be null")
    private List<String> options;

    @NotNull(message = "correctAnswer cannot be null")
    private String correctAnswer;

}
