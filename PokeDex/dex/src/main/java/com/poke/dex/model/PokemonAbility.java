package com.poke.dex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Pokemon pokemon;

    @Id
    @ManyToOne
    @JoinColumn(name = "ability_id", nullable = false)
    private Ability ability;

    private Boolean isHidden;
}

