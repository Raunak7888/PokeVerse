package com.poke.dex.repository;

import com.poke.dex.model.Pokemon;
import com.poke.dex.model.PokemonMove;
import com.poke.dex.model.PokemonMoveId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonMoveRepository extends JpaRepository<PokemonMove, PokemonMoveId> {
    List<PokemonMove> findByPokemon(Pokemon pokemon);
}
