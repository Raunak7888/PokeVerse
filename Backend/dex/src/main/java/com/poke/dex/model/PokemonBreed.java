package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "pokemon_breeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PokemonBreed {
    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemon;

    @ElementCollection
    @CollectionTable(name = "pokemon_egg_groups", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "egg_group")
    private List<String> eggGroups;

    private int hatchTime;
    private float male;
    private float female;
    private String growthRate;
}
