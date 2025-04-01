package com.poke.matrix.controller;

import com.poke.matrix.dto.PokemonDto;
import com.poke.matrix.service.PokemonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private static final Logger logger = LoggerFactory.getLogger(PokemonController.class);
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPokemon(@RequestBody PokemonDto pokemonDto) {
        logger.info("Received request to add Pokémon: {}", pokemonDto.getName());
        return pokemonService.addPokemon(pokemonDto);
    }
}
