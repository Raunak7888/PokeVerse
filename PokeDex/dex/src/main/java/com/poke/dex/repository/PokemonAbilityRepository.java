package com.poke.dex.repository;

import com.poke.dex.model.Pokemon;
import com.poke.dex.model.PokemonAbility;
import com.poke.dex.model.PokemonAbilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PokemonAbilityRepository extends JpaRepository<PokemonAbility, PokemonAbilityId> {
    List<PokemonAbility> findByPokemon(Pokemon pokemon);
}
