package com.poke.dex.repository;

import com.poke.dex.model.Pokemon;
import com.poke.dex.model.PokemonStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonStatsRepository extends JpaRepository<PokemonStats, Integer> {
    Optional<PokemonStats> findByPokemon(Pokemon pokemon);
}
