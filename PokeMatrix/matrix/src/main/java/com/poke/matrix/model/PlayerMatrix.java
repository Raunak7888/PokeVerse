package com.poke.matrix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Player_Matrix")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerMatrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)  // Foreign key to Player
    private Player player;

    @ManyToOne
    @JoinColumn(name = "matrix_id", nullable = false)  // Foreign key to MatrixStructure
    private MatrixStructure matrix;

    private int rowId;
    private int colId;

    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemonId;

    private boolean isValid;
}
