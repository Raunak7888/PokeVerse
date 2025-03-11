package com.poke.dex.repository;

import com.poke.dex.model.PokemonImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonImageRepository extends JpaRepository<PokemonImage, Integer> {}
