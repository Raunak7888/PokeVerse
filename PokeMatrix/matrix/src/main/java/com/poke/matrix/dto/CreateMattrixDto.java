package com.poke.matrix.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMattrixDto {
    private Long userId;
    private int rows;
    private int columns;
}
