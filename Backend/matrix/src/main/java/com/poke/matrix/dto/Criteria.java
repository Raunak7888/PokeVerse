package com.poke.matrix.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Criteria {
    private final String[] types = {
            "grass", "fire", "water", "normal", "electric", "ice",
            "fighting", "poison", "ground", "flying", "psychic", "bug",
            "rock", "ghost", "dragon", "dark", "steel", "fairy"
    };

    private final String[] regions = {
            "Kanto", "Johto", "Hoenn", "Sinnoh", "Unova",
            "Kalos", "Alola", "Galar", "Paldea"
    };

    private final String[] abilities = {
            "Overgrow", "Blaze", "Torrent", "Static", "Levitate", "Pressure",
            "Intimidate", "Swift Swim", "Chlorophyll", "Synchronize",
            "Guts", "Technician", "Adaptability", "Mold Breaker", "Magic Guard"
    };

    private final String[] evolutionStages = {
            "Basic", "Stage 1", "Stage 2", "Mega Evolution", "Gigantamax", "Regional Form"
    };

    private final String[] legendaryStatus = {
            "Normal", "Legendary", "Mythical", "Ultra Beast", "Paradox"
    };

    private final String[] bodyShapes = {
            "Head Only", "Serpentine Body", "Fish-Like", "Quadruped",
            "Wings", "Tentacles", "Humanoid", "Insectoid"
    };

    private final String[] colors = {
            "Red", "Blue", "Yellow", "Green", "Black", "Brown",
            "Purple", "Gray", "White", "Pink"
    };

    private final float[] sizesInMeterBy10 = {
            0.1f,0.5f,1f,3f,5f,10f
    };

    private final int[] weightsInKgBy10 = {
            1,5,10,25,50,100,300,500
    };
}
