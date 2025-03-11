package com.poke.dex.repository;

import com.poke.dex.model.PokemonStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonStatsRepository extends JpaRepository<PokemonStats, Integer> {}
