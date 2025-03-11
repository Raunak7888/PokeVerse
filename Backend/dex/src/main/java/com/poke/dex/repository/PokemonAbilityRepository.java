package com.poke.dex.repository;

import com.poke.dex.model.PokemonAbility;
import com.poke.dex.model.PokemonAbilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonAbilityRepository extends JpaRepository<PokemonAbility, PokemonAbilityId> {}
