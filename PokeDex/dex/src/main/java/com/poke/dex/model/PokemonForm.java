package com.poke.dex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pokemon_forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PokemonFormId.class) // ✅ Use IdClass for composite key
public class PokemonForm {
    @Id
    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    @JsonIgnore
    private Pokemon pokemon;

    @Id
    private String formName; // ✅ Make this part of the composite key

    private String name;
    private Boolean isMega;
    private Boolean isGigantamax;
}
