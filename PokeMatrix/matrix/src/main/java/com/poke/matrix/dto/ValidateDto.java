package com.poke.matrix.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateDto {
    private Long matrixId;
    private Long pokemonId;
    private Long rowId;
    private Long colId;
    private Long userId;
    private boolean isValid;
}
