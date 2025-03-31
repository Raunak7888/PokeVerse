package com.poke.dex.controller;

import com.poke.dex.dto.*;
import com.poke.dex.service.PokemonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Hello from pokedex service!");
    }

    @PostMapping("/type")
    public ResponseEntity<?> addType(@RequestBody TypeDto type) {
        if (type == null) {
            return ResponseEntity.badRequest().body("Invalid type data.");
        }
        return ResponseEntity.ok(pokemonService.addType(type));
    }

    @PostMapping("/damage-relation")
    public ResponseEntity<?> addDamageRelation(@RequestBody TypeDto typeDto) {
        if (typeDto == null) {
            return ResponseEntity.badRequest().body("Invalid damage relation data.");
        }
        return ResponseEntity.ok(pokemonService.addDamageRelation(typeDto));
    }

    @PostMapping("/ability")
    public ResponseEntity<?> addAbility(@RequestBody OnlyAbilityDto ability){
        if (ability == null) {
            return ResponseEntity.badRequest().body("Invalid ability data.");
        }
        return pokemonService.addAbility(ability);
    }

    @PostMapping("/move")
    public ResponseEntity<?> addMove(@RequestBody MoveDto moveDto) {
        if (moveDto == null) {
            return ResponseEntity.badRequest().body("Invalid move data.");
        }
        return pokemonService.addMove(moveDto);
    }

    @PostMapping("/pokemon")
    public ResponseEntity<?> addPokemon(@RequestBody PokemonDto dto) {

        pokemonService.addPokemon(dto);
        return ResponseEntity.ok("✅ Pokémon added successfully!");

    }

    @GetMapping("/range/{startId}/{endId}")
    public ResponseEntity<?> getPokemonsInRange(@PathVariable Integer startId, @PathVariable Integer endId) {
        return pokemonService.getPokemonsInRange(startId, endId);
    }

    @GetMapping("/type/{typeId}/{startId}/{endId}")
    public ResponseEntity<?> getPokemonsByType(@PathVariable int typeId, @PathVariable Integer startId, @PathVariable Integer endId) {
        return pokemonService.getPokemonByType(typeId, startId, endId);
    }

    @GetMapping("/generation/{generation}/{startId}/{endId}")
    public ResponseEntity<?> getPokemonsByGeneration(@PathVariable int generation, @PathVariable Integer startId, @PathVariable Integer endId) {
        return pokemonService.getPokemonsByGeneration(generation, startId, endId);
    }

    @GetMapping("/abilities/{ability}/{startId}/{endId}")
    public ResponseEntity<?> getPokemonsByAbilities(@PathVariable String ability, @PathVariable Integer startId, @PathVariable Integer endId) {
        return pokemonService.getPokemonsByAbilities(ability, startId, endId);
    }

    @GetMapping("/legendary/{startId}/{endId}")
    public ResponseEntity<?> getLegendaryPokemons(@PathVariable Integer startId, @PathVariable Integer endId) {
        return pokemonService.getLegendaryPokemons(startId, endId);
    }

    @GetMapping("/mythical/{startId}/{endId}")
    public ResponseEntity<?> getMythicalPokemons(@PathVariable Integer startId, @PathVariable Integer endId) {
        return pokemonService.getMythicalPokemons(startId, endId);
    }

    @GetMapping("/pokemon/{pokemonId}")
    public ResponseEntity<?> getPokemonById(@PathVariable int pokemonId) {
        return pokemonService.getPokemonById(pokemonId);
    }
}
