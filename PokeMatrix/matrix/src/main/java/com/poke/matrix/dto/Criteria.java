package com.poke.matrix.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
public class Criteria {

    private final String[] criteria = {
            "regions", "abilities", "evolutionStages", "legendaryStatus",
            "bodyShapes", "colors", "sizesInMeterBy10", "weightsInKgBy10"
    };

    private final Map<String, String[]> dataMap = Map.of(
            "regions", new String[]{"Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos", "Alola", "Galar", "Paldea"},
            "abilities", new String[]{"Overgrow", "Blaze", "Torrent", "Static", "Levitate", "Pressure", "Intimidate", "Swift Swim"},
            "evolutionStages", new String[]{"Basic", "Stage 1", "Stage 2", "Mega", "Gigantamax"},
            "legendaryStatus", new String[]{"Normal", "Legendary", "Mythical", "Ultra Beast"},
            "bodyShapes", new String[]{"Head Only", "Serpentine Body", "Fish-Like", "Quadruped", "Wings", "Tentacles", "Humanoid", "Insectoid"},
            "colors", new String[]{"Red", "Blue", "Yellow", "Green", "Black", "Brown", "Purple", "Gray", "White", "Pink"},
            "sizesInMeterBy10", new String[]{"Less than 0.1m", "More than 0.1m", "Less than 0.5m", "More than 0.5m", "Less than 1m", "More than 1m", "Less than 3m", "More than 3m", "Less than 5m", "More than 5m", "Less than 10m", "More than 10m"},
            "weightsInKgBy10", new String[]{"Less than 5kg", "More than 5kg", "Less than 10kg", "More than 10kg", "Less than 25kg", "More than 25kg", "Less than 50kg", "More than 50kg", "Less than 100kg", "More than 100kg", "Less than 300kg", "More than 300kg", "Less than 500kg", "More than 500kg"}
    );

    public String[] getCriteria() {
        return criteria;
    }

    public Map<String, String[]> getDataMap() {
        return dataMap;
    }
}
