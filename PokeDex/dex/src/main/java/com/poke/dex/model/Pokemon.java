package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pokemon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pokemon {
    @Id
    private Integer id;
    private String name;
    private Integer height;
    private Integer weight;

    @ManyToOne
    @JoinColumn(name = "type1", referencedColumnName = "id")
    private Type type1;

    @ManyToOne
    @JoinColumn(name = "type2", referencedColumnName = "id")
    private Type type2;

    private Integer baseExperience;
    private Integer baseCatchRate;
    private Integer baseHappiness;
    private String flavorText;
    private String genus;
    private String habitat;
    private String color;
    private String shape;
    private String generation;
    private Boolean legendary;
    private Boolean mythical;
}
