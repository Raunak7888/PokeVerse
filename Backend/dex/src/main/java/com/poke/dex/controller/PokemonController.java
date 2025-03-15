package com.poke.dex.controller;

import com.poke.dex.dto.MoveDto;
import com.poke.dex.dto.OnlyAbilityDto;
import com.poke.dex.dto.PokemonDto;
import com.poke.dex.dto.TypeDto;
import com.poke.dex.service.PokemonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
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
        return pokemonService.addPokemon(dto);
    }


}
