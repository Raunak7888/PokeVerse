package com.poke.matrix.service;

import com.poke.matrix.controller.PokemonController;
import com.poke.matrix.dto.PokemonDto;
import com.poke.matrix.model.Pokemon;
import com.poke.matrix.repository.PokemonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PokemonService {
    private static final Logger logger = LoggerFactory.getLogger(PokemonController.class);
    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public ResponseEntity<String> addPokemon(PokemonDto pokemonDto) {
        // Check if Pokémon already exists
        Optional<Pokemon> existingPokemon = pokemonRepository.findById(pokemonDto.getId());
        if (existingPokemon.isPresent()) {
            logger.warn("Pokemon with ID {} already exists!", pokemonDto.getId());
            return ResponseEntity.badRequest().body("Pokemon with ID " + pokemonDto.getId() + " already exists.");
        }

        // Convert DTO to Entity
        Pokemon pokemon = new Pokemon(
                pokemonDto.getId(),
                pokemonDto.getName(),
                pokemonDto.getHeight(),
                pokemonDto.getWeight(),
                pokemonDto.getType1(),
                pokemonDto.getType2(),
                pokemonDto.getRegion(),
                pokemonDto.getEvolutionStage(),
                pokemonDto.getLegendaryStatus(),
                pokemonDto.getColor(),
                pokemonDto.getBodyShape(),
                pokemonDto.getAbilities()
        );

        logger.debug("Pokemon entity created: {}", pokemon);

        // Save to database
        pokemonRepository.save(pokemon);
        logger.info("Pokemon {} (ID: {}) saved successfully!", pokemon.getName(), pokemon.getId());

        return ResponseEntity.ok("Pokemon " + pokemon.getName() + " added successfully.");
    }
}
