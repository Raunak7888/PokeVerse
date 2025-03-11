package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pokemon_abilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PokemonAbilityId.class)
public class PokemonAbility {
    @Id
    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemon;

    @Id
    @ManyToOne
    @JoinColumn(name = "ability_id", nullable = false)
    private Ability ability;

    private Boolean isHidden;
}

