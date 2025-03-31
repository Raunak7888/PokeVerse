package com.poke.dex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pokemon_moves")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PokemonMoveId.class)
public class PokemonMove {
    @Id
    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    @JsonIgnore
    private Pokemon pokemon;

    @Id
    @ManyToOne
    @JoinColumn(name = "move_id", nullable = false)
    private Move move;

    private String learnMethod;
    private Integer levelLearnedAt;
}
