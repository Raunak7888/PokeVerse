package com.poke.dex.repository;

import com.poke.dex.model.Pokemon;
import com.poke.dex.model.PokemonImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonImageRepository extends JpaRepository<PokemonImage, Integer> {
    Optional<PokemonImage> findByPokemon(Pokemon pokemon);
}
