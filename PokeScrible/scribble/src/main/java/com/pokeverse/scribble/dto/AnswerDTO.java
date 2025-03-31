package com.pokeverse.scribble.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerDTO {
    private Long userId;
    private String answer;
    private int time;
}
