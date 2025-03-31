package com.poke.dex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor  // Constructor for JPQL Query
public class PokemonCardDto {
    private int id;
    private String name;
    private String imageUrl;
    private String type;
    private String type2;
    private String generation;
}
