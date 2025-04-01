package com.poke.dex.repository;

import com.poke.dex.dto.PokemonCardDto;
import com.poke.dex.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    @Query("""
    SELECT new com.poke.dex.dto.PokemonCardDto(
        p.id, p.name, pi.imageUrl, t1.name, COALESCE(t2.name, ''), p.generation
    )
    FROM Pokemon p
    JOIN PokemonImage pi ON pi.pokemon = p
    JOIN p.type1 t1
    LEFT JOIN p.type2 t2
    WHERE p.id BETWEEN :startId AND :endId
    """)
    List<PokemonCardDto> getPokemonCardDtoByRange(@Param("startId") Integer startId, @Param("endId") Integer endId);

    @Query("""
    SELECT new com.poke.dex.dto.PokemonCardDto(
        p.id, p.name, pi.imageUrl, t1.name, COALESCE(t2.name, ''), p.generation
    )
    FROM Pokemon p
    JOIN PokemonImage pi ON pi.pokemon = p
    JOIN p.type1 t1
    LEFT JOIN p.type2 t2
    WHERE (t1.id = :typeId OR t2.id = :typeId) AND p.id BETWEEN :startId AND :endId
    """)
    List<PokemonCardDto> getPokemonByType(@Param("typeId") int typeId, @Param("startId") Integer startId, @Param("endId") Integer endId);


    @Query("""
    SELECT new com.poke.dex.dto.PokemonCardDto(
        p.id, p.name, pi.imageUrl, t1.name, COALESCE(t2.name, ''), p.generation
    )
    FROM Pokemon p
    JOIN PokemonImage pi ON pi.pokemon = p
    JOIN p.type1 t1
    LEFT JOIN p.type2 t2
    WHERE p.generation = :generation AND p.id BETWEEN :startId AND :endId
    """)
    List<PokemonCardDto> getPokemonByGeneration(@Param("generation") String generation, @Param("startId") Integer startId, @Param("endId") Integer endId);

    @Query("""
    SELECT new com.poke.dex.dto.PokemonCardDto(
        p.id, p.name, pi.imageUrl, t1.name, COALESCE(t2.name, ''), p.generation
    )
    FROM Pokemon p
    JOIN PokemonImage pi ON pi.pokemon = p
    JOIN p.type1 t1
    LEFT JOIN p.type2 t2
    JOIN PokemonAbility pa ON pa.pokemon = p
    JOIN pa.ability a
    WHERE a.name = :ability AND p.id BETWEEN :startId AND :endId
""")
    List<PokemonCardDto> getPokemonByAbility(@Param("ability") String ability,
                                             @Param("startId") Integer startId,
                                             @Param("endId") Integer endId);

    @Query("""
    SELECT new com.poke.dex.dto.PokemonCardDto(
        p.id, p.name, pi.imageUrl, t1.name, COALESCE(t2.name, ''), p.generation
    )
    FROM Pokemon p
    JOIN PokemonImage pi ON pi.pokemon = p
    JOIN p.type1 t1
    LEFT JOIN p.type2 t2
    WHERE p.legendary = true AND p.id BETWEEN :startId AND :endId
""")
    List<PokemonCardDto> getLegendaryPokemons(@Param("startId") Integer startId,
                                              @Param("endId") Integer endId);

    @Query("""
    SELECT new com.poke.dex.dto.PokemonCardDto(
        p.id, p.name, pi.imageUrl, t1.name, COALESCE(t2.name, ''), p.generation
    )
    FROM Pokemon p
    JOIN PokemonImage pi ON pi.pokemon = p
    JOIN p.type1 t1
    LEFT JOIN p.type2 t2
    WHERE p.mythical = true AND p.id BETWEEN :startId AND :endId
""")
    List<PokemonCardDto> getMythicalPokemons(@Param("startId") Integer startId,
                                             @Param("endId") Integer endId);

    @Query(value = """
    SELECT json_build_object(
        'id', p.id,
        'name', p.name,
        'height', p.height,
        'weight', p.weight,
        'base_experience', p.base_experience,
        'base_catch_rate', p.base_catch_rate,
        'base_happiness', p.base_happiness,
        'flavor_text', p.flavor_text,
        'genus', p.genus,
        'habitat', p.habitat,
        'color', p.color,
        'shape', p.shape,
        'generation', p.generation,
        'legendary', p.legendary,
        'mythical', p.mythical,
        'type1', t1.name,
        'type2', COALESCE(t2.name, NULL),
        'abilities', (
            SELECT json_agg(json_build_object(
                'id', a.id,
                'name', a.name,
                'description', a.description,
                'generation', a.generation
            ))
            FROM pokemon_abilities pa
            JOIN abilities a ON pa.ability_id = a.id
            WHERE pa.pokemon_id = p.id
        ),
        'moves', (
            SELECT json_agg(json_build_object(
                'id', m.id,
                'name', m.move_name,
                'type', m.move_type,
                'power', m.power,
                'accuracy', m.accuracy,
                'pp', m.pp,
                'effect', m.effect,
                'damage_type', m.damage_type,
                'generation', m.generation
            ))
            FROM pokemon_moves pm
            JOIN moves m ON pm.move_id = m.id
            WHERE pm.pokemon_id = p.id
        ),
        'stats', json_build_object(
            'hp', ps.hp,
            'attack', ps.attack,
            'defense', ps.defense,
            'special_attack', ps.special_attack,
            'special_defense', ps.special_defense,
            'speed', ps.speed
        ),
        'breeding', json_build_object(
                          'egg_groups', (
                              SELECT json_agg(peg.egg_group)
                              FROM pokemon_egg_groups peg
                              WHERE peg.pokemon_id = pb.pokemon_id
                          ),
                          'hatch_time', pb.hatch_time,
                          'male_ratio', pb.male,
                          'female_ratio', pb.female,
                          'growth_rate', pb.growth_rate
                      ),
            
        'forms', (
            SELECT json_agg(json_build_object(
                'form_name', pf.form_name,
                'name', pf.name,
                'is_mega', pf.is_mega,
                'is_gigantamax', pf.is_gigantamax
            ))
            FROM pokemon_forms pf
            WHERE pf.pokemon_id = p.id
        ),
        'damage_relations', (
            SELECT json_agg(json_build_object(
                'type', dt.name,
                'related_type', drt.name,
                'times_damage', dr.times_damage
            ))
            FROM damage_relations dr
            JOIN types dt ON dr.type_id = dt.id
            JOIN types drt ON dr.related_type_id = drt.id
            WHERE dr.type_id = p.type1 OR dr.type_id = p.type2
        ),
        'image', pi.image_url
    )
    FROM pokemon p
    LEFT JOIN types t1 ON p.type1 = t1.id
    LEFT JOIN types t2 ON p.type2 = t2.id
    LEFT JOIN pokemon_stats ps ON ps.pokemon_id = p.id
    LEFT JOIN pokemon_breeds pb ON pb.pokemon_id = p.id
    LEFT JOIN pokemon_image pi ON pi.pokemon_id = p.id
    WHERE p.id = :pokemonId
""", nativeQuery = true)
    Optional<String> findFullPokemonData(@Param("pokemonId") int pokemonId);

}
