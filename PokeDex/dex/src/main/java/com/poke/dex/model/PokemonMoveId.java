package com.poke.dex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonMoveId implements java.io.Serializable {
    private Integer pokemon;
    private Integer move;
}