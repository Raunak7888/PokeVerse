package com.poke.dex.repository;

import com.poke.dex.model.PokemonMove;
import com.poke.dex.model.PokemonMoveId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonMoveRepository extends JpaRepository<PokemonMove, PokemonMoveId> {}
