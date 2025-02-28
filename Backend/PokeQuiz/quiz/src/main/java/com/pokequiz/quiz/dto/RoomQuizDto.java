package com.pokequiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomQuizDto {
    private Long quizId;
    private Long roomId;
    private LocalDateTime createdAt;
}