package com.poke.dex.dto;

import com.poke.dex.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PokeDetailDto {
    private int id;
    private String name;
    private int height;
    private int weight;

    private String type1;
    private String type2;

    private List<Ability> abilities;
    private List<Move> moves;

    private PokemonStats stats;
    private PokemonBreed breed;
    private PokemonForm form;
    private PokemonImage image;


}
