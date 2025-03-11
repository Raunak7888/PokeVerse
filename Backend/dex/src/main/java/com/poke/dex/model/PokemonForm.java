package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pokemon_forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PokemonFormId.class)
public class PokemonForm {
    @Id
    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemon;

    @Id
    private Integer formId;

    private String name;
    private String formName;
    private Boolean isMega;
    private Boolean isGigantamax;
}

