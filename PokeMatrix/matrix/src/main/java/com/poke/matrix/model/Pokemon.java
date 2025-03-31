package com.poke.matrix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pokemon")
public class Pokemon {
    @Id
    private Long id;
    private String name;           // Pokémon's Name

    private int height;            // Height in decimeters (from PokeAPI)

    private int weight;            // Weight in hectograms (from PokeAPI)

    private String type1;          // Primary Pokémon Type

    private String type2;          // Secondary Type (can be null for single-type Pokémon)

    private String region;         // Origin Region (e.g., Kanto, Johto, etc.)

    private String evolutionStage; // Basic, Stage 1, Stage 2, Mega Evolution, etc.

    private String legendaryStatus; // Normal, Legendary, Mythical, Ultra Beast, Paradox

    private String color;          // Primary color (for classification)

    private String bodyShape;      // Physical shape (e.g., Serpentine, Quadruped)

    @ElementCollection
    @CollectionTable(name = "pokemon_abilities", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "ability")
    private List<String> abilities; // List of Pokémon's abilities
}
