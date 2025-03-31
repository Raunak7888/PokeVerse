package com.poke.dex.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PokemonFormId implements Serializable {
    private Integer pokemon; // Match the field type in `PokemonForm`
    private String formName; // Unique identifier for the form

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonFormId that = (PokemonFormId) o;
        return Objects.equals(pokemon, that.pokemon) &&
                Objects.equals(formName, that.formName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pokemon, formName);
    }
}

