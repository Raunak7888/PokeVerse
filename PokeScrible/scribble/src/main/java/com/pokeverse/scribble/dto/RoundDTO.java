package com.pokeverse.scribble.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundDTO {
    private Long userId;
    private String toGuess;
}
