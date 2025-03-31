package com.poke.matrix.repository;

import com.poke.matrix.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findByRegionAndType1OrType2(String region, String type1, String type2);
}
