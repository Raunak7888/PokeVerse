package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pokemon_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonStats {
    @Id
    private Integer pokemonId; // Define an explicit primary key

    @OneToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    @MapsId
    private Pokemon pokemon;

    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer specialAttack;
    private Integer specialDefense;
    private Integer speed;
}
