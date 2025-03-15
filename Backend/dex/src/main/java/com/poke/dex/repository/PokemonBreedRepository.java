package com.poke.dex.repository;

import com.poke.dex.model.PokemonBreed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonBreedRepository extends JpaRepository<PokemonBreed,Long> {
}
