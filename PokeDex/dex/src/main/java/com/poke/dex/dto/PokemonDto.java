package com.poke.dex.dto;

import com.poke.dex.model.PokemonForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PokemonDto {
    private Integer id;
    private String name;
    private Integer height;
    private Integer weight;
    private Integer baseExperience;
    private List<Type> types; // List of Type IDs
    private List<AbilityDto> abilities;
    private StatsDto stats;
    private SpeciesDto species;
    private List<MoveDto> moves;
    private BreedingDto breeding;
    private Integer baseCatchRate;
    private Integer baseHappiness;
    private String growthRate;
    private String spriteUrl; // Image URL
    private PokemonForm form;

    @Getter
    @Setter
    public static class Type {
        private Integer id;
        private String name;
        private String generation;
    }

    @Getter
    @Setter
    public static class AbilityDto {
        private Integer id;
        private String name;
        private Boolean isHidden;
        private String effect;
    }

    @Getter
    @Setter
    public static class StatsDto {
        private Integer hp;
        private Integer attack;
        private Integer defense;
        private Integer specialAttack;
        private Integer specialDefense;
        private Integer speed;
    }

    @Getter
    @Setter
    public static class SpeciesDto {
        private String flavorText;
        private String genus;
        private String habitat;
        private String color;
        private String shape;
        private String generation;
        private Boolean legendary;
        private Boolean mythical;
    }

    @Getter
    @Setter
    public static class MoveDto {
        private Integer id;
        private String name;
        private String type;
        private Integer power;
        private Integer accuracy;
        private String pp;
        private String generation;
        private String description;
        private String learnMethod;
        private Integer levelLearnedAt;
    }

    @Getter
    @Setter
    public static class BreedingDto {
        private List<String> eggGroups;
        private Integer hatchTime;
        private GenderRatioDto genderRatio;
    }

    @Getter
    @Setter
    public static class GenderRatioDto {
        private Float male;
        private Float female;
    }

    @Getter
    @Setter
    public static class FormDto{
        private String name;
        private String formName;
        private Boolean isMega;
        private Boolean isGigantamax;
    }
}
