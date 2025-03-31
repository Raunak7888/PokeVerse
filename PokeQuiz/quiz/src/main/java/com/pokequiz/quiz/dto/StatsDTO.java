package com.pokequiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private Long userId;
    private String username;
    private int totalPoints;
    private List<DetailedAnswer> detailedAnswers;
}

