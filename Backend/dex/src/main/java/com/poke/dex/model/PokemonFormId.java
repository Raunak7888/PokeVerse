package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonFormId implements java.io.Serializable {
    private Integer pokemon;
    private Integer formId;
}
