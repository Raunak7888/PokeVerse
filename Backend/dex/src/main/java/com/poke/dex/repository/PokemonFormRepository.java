package com.poke.dex.repository;

import com.poke.dex.model.PokemonForm;
import com.poke.dex.model.PokemonFormId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonFormRepository extends JpaRepository<PokemonForm, PokemonFormId> {}
