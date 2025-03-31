package com.poke.dex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pokemon_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonImage {
    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    @JsonIgnore
    private Pokemon pokemon;

    private String imageUrl;
}
