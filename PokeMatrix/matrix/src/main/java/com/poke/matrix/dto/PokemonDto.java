package com.poke.matrix.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PokemonDto {
    private Long id;
    private String name;
    private int height;
    private int weight;
    private String type1;
    private String type2;
    private String region;
    private String evolutionStage;
    private String legendaryStatus;
    private String color;
    private String bodyShape;
    private List<String> abilities;

}
