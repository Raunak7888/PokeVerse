package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Pokemon pokemon;

    private String imageUrl;
}
