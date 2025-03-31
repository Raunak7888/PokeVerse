package com.pokequiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Long userId;
    private String name;
    private int score;
    private Long roomId;
    private LocalDateTime createdAt;
}
