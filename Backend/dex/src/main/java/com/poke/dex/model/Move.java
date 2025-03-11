package com.poke.dex.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "moves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    @Id
    private Integer id;
    private String moveName;
    private String moveType;
    private Integer power;
    private Integer accuracy;
    private Integer pp;
    private String effect;
    private String damageType;
    private String generation;
}

