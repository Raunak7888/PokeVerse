package com.poke.dex.repository;

import com.poke.dex.model.Pokemon;
import com.poke.dex.model.PokemonBreed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonBreedRepository extends JpaRepository<PokemonBreed,Long> {
    Optional<PokemonBreed> findByPokemon(Pokemon pokemon);
}
