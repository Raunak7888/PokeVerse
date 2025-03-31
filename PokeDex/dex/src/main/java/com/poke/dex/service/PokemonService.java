package com.poke.dex.service;

import com.poke.dex.dto.*;
import com.poke.dex.model.*;
import com.poke.dex.repository.*;
import com.poke.dex.util.RomanNumeralsUtil;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class PokemonService {

    private final TypeRepository typeRepository;
    private final DamageRelationRepository damageRepository;
    private final AbilityRepository abilityRepository;
    private final MoveRepository moveRepository;
    private final PokemonRepository pokemonRepository;
    private final PokemonAbilityRepository pokemonAbilityRepository;
    private final PokemonFormRepository pokemonFormRepository;
    private final PokemonStatsRepository pokemonStatsRepository;
    private final PokemonImageRepository pokemonImageRepository;
    private final PokemonMoveRepository pokemonMoveRepository;
    private final PokemonBreedRepository pokemonBreedRepository;

    public PokemonService(TypeRepository typeRepository, DamageRelationRepository damageRepository, AbilityRepository abilityRepository, MoveRepository moveRepository, PokemonRepository pokemonRepository, PokemonAbilityRepository pokemonAbilityRepository, PokemonFormRepository pokemonFormRepository, PokemonStatsRepository pokemonStatsRepository, PokemonImageRepository pokemonImageRepository, PokemonMoveRepository pokemonMoveRepository, PokemonBreedRepository pokemonBreedRepository) {
        this.typeRepository = typeRepository;
        this.damageRepository = damageRepository;
        this.abilityRepository = abilityRepository;
        this.moveRepository = moveRepository;
        this.pokemonRepository = pokemonRepository;
        this.pokemonAbilityRepository = pokemonAbilityRepository;
        this.pokemonFormRepository = pokemonFormRepository;
        this.pokemonStatsRepository = pokemonStatsRepository;
        this.pokemonImageRepository = pokemonImageRepository;
        this.pokemonMoveRepository = pokemonMoveRepository;
        this.pokemonBreedRepository = pokemonBreedRepository;
    }


    public Type addType(TypeDto dto) {
        Type type = new Type(dto.getId(), dto.getName(), dto.getGeneration());
        return typeRepository.save(type);
    }

    public DamageRelation addDamageRelation(TypeDto dto) {
        Type type = typeRepository.findById(dto.getType()).orElseThrow(() -> new IllegalArgumentException("Type not found"));
        Type related = typeRepository.findById(dto.getRelatedType()).orElseThrow(() -> new IllegalArgumentException("Related type not found"));

        DamageRelation relation = new DamageRelation(type, related, dto.getTimesDamage());
        return damageRepository.save(relation);
    }

    public ResponseEntity<String> addAbility(OnlyAbilityDto dto) {
        if (abilityRepository.existsById(dto.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ability already exists: " + dto.getName());
        }

        Ability ability = new Ability();
        ability.setId(dto.getId());
        ability.setName(dto.getName());
        ability.setDescription(dto.getDescription());
        ability.setGeneration(dto.getGeneration());

        abilityRepository.save(ability);

        return ResponseEntity.status(HttpStatus.CREATED).body("Ability added: " + dto.getName());
    }

    public ResponseEntity<String> addMove(MoveDto moveDto) {
        if (moveRepository.existsById(moveDto.getId())) {
            return ResponseEntity.badRequest().body("Move already exists: " + moveDto.getName());
        }

        Move move = new Move(
                moveDto.getId(),
                moveDto.getName(),
                moveDto.getType(),
                moveDto.getPower(),
                moveDto.getAccuracy(),
                moveDto.getPp(),
                moveDto.getEffect(),
                moveDto.getDamageType(),
                moveDto.getGeneration()
        );

        moveRepository.save(move);

        return ResponseEntity.ok("Move Added: " + moveDto.getName());
    }

    // 🔥 Enables logging (Lombok)
    @Transactional
    public ResponseEntity<?> addPokemon(PokemonDto dto) {
        log.info("🔄 Received request to add Pokémon: {}", dto.getName());

        // ✅ Check if Pokémon already exists
        if (pokemonRepository.existsById(dto.getId())) {
            log.warn("⚠️ Pokémon already exists: {}", dto.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pokemon already exists: " + dto.getName());
        }

        try {
            // ✅ Fetch Types
            log.info("🔎 Fetching types for Pokémon: {}", dto.getName());
            Type type1 = typeRepository.findById(dto.getTypes().getFirst().getId()).orElseThrow(() -> new RuntimeException("Type not found"));
            Type type2 = (dto.getTypes().size() > 1) ?
                    typeRepository.findById(dto.getTypes().get(1).getId()).orElse(null) : null;
            log.info("✅ Types fetched: Type1 = {}, Type2 = {}", type1.getName(), (type2 != null ? type2.getName() : "None"));

            // ✅ Create & Save Pokémon Entity
            log.info("💾 Saving Pokémon: {}", dto.getName());
            Pokemon pokemon = new Pokemon(
                    dto.getId(), dto.getName(), dto.getHeight(), dto.getWeight(),
                    type1, type2, dto.getBaseExperience(), dto.getBaseCatchRate(), dto.getBaseHappiness(),
                    dto.getSpecies().getFlavorText(), dto.getSpecies().getGenus(), dto.getSpecies().getHabitat(),
                    dto.getSpecies().getColor(), dto.getSpecies().getShape(), dto.getSpecies().getGeneration(),
                    dto.getSpecies().getLegendary(), dto.getSpecies().getMythical()
            );
            pokemonRepository.save(pokemon);
            log.info("✅ Pokémon saved: {}", dto.getName());

            // ✅ Save Image
            if (dto.getSpriteUrl() != null) {
                log.info("🖼️ Saving Pokémon Image for: {}", dto.getName());
                PokemonImage image = new PokemonImage(dto.getId(), pokemon, dto.getSpriteUrl());
                pokemonImageRepository.save(image);
                log.info("✅ Pokémon Image saved: {}", dto.getSpriteUrl());
            }

            // ✅ Save Abilities
            log.info("🛠️ Saving abilities for: {}", dto.getName());
            for (PokemonDto.AbilityDto ability : dto.getAbilities()) {
                Ability abilityEntity = abilityRepository.findById(ability.getId()).orElseThrow(() -> new RuntimeException("Ability not found"));
                PokemonAbility pokemonAbility = new PokemonAbility(pokemon, abilityEntity, ability.getIsHidden());
                pokemonAbilityRepository.save(pokemonAbility);
                log.info("✅ Ability saved: {} (Hidden: {})", abilityEntity.getName(), ability.getIsHidden());
            }

            // ✅ Save Stats
            log.info("📊 Saving stats for: {}", dto.getName());
            PokemonStats stats = new PokemonStats(
                    dto.getId(), pokemon, dto.getStats().getHp(), dto.getStats().getAttack(),
                    dto.getStats().getDefense(), dto.getStats().getSpecialAttack(),
                    dto.getStats().getSpecialDefense(), dto.getStats().getSpeed()
            );
            pokemonStatsRepository.save(stats);
            log.info("✅ Stats saved for: {}", dto.getName());

            // ✅ Save Breeding Data
            log.info("🧬 Saving breeding data for: {}", dto.getName());
            PokemonBreed breed = new PokemonBreed(
                    dto.getId(), pokemon, dto.getBreeding().getEggGroups(),
                    dto.getBreeding().getHatchTime(), dto.getBreeding().getGenderRatio().getMale(),
                    dto.getBreeding().getGenderRatio().getFemale(), dto.getGrowthRate()
            );
            pokemonBreedRepository.save(breed);
            log.info("✅ Breeding data saved for: {}", dto.getName());

            // ✅ Save Pokémon Form
            log.info("🌀 Saving Pokémon form for: {}", dto.getName());
            PokemonForm pokemonForm = new PokemonForm(
                    pokemon, dto.getForm().getName(), dto.getForm().getFormName(),
                    dto.getForm().getIsMega(), dto.getForm().getIsGigantamax()
            );
            pokemonFormRepository.save(pokemonForm);
            log.info("✅ Pokémon Form saved for: {}", dto.getName());

            // ✅ Save Moves
            log.info("⚔️ Saving moves for: {}", dto.getName());
            for (PokemonDto.MoveDto move : dto.getMoves()) {
                Move moveEntity = moveRepository.findById(move.getId()).orElseThrow(() -> new RuntimeException("Move not found"));
                PokemonMove pokemonMove = new PokemonMove(pokemon, moveEntity, move.getLearnMethod(), move.getLevelLearnedAt());
                pokemonMoveRepository.save(pokemonMove);
                log.info("✅ Move saved: {} (Learn Method: {}, Level: {})", moveEntity.getMoveName(), move.getLearnMethod(), move.getLevelLearnedAt());
            }

            log.info("🎉 Pokémon successfully added: {}", dto.getName());
            return ResponseEntity.ok("✅ Pokémon added: " + dto.getName());

        } catch (Exception e) {
            log.error("❌ Error while adding Pokémon: {}", dto.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error adding Pokémon: " + e.getMessage());
        }
    }


    public ResponseEntity<?> getPokemonsInRange(Integer startId, Integer endId) {
        if (isValidRange(startId, endId)) {
            return ResponseEntity.badRequest().body("Invalid ID range. Must be between 1 and 1025.");
        }

        List<PokemonCardDto> pokemonList = pokemonRepository.getPokemonCardDtoByRange(startId, endId);
        return ResponseEntity.ok(pokemonList);
    }

    public ResponseEntity<?> getPokemonByType(int typeId, Integer startId, Integer endId) {
        if (typeId < 1 || typeId > 18 || isValidRange(startId, endId)) {
            return ResponseEntity.badRequest().body("Invalid Type ID or ID range.");
        }

        List<PokemonCardDto> pokemonList = pokemonRepository.getPokemonByType(typeId, startId, endId);
        return ResponseEntity.ok(pokemonList);
    }

    public ResponseEntity<?> getPokemonsByGeneration(int generation, Integer startId, Integer endId) {
        if (generation < 1 || generation > 9 || isValidRange(startId, endId)) {
            return ResponseEntity.badRequest().body("Invalid Generation or ID range.");
        }

        String generationLabel = "generation-" + RomanNumeralsUtil.toRoman(generation); // Convert to format like 'generation-i'
        List<PokemonCardDto> pokemonList = pokemonRepository.getPokemonByGeneration(generationLabel, startId, endId);
        return ResponseEntity.ok(pokemonList);
    }

    public ResponseEntity<?> getPokemonsByAbilities(String ability, Integer startId, Integer endId) {
        if (ability == null || ability.isBlank() || isValidRange(startId, endId)) {
            return ResponseEntity.badRequest().body("Invalid Ability name or ID range.");
        }

        List<PokemonCardDto> pokemonList = pokemonRepository.getPokemonByAbility(ability.toLowerCase(), startId, endId);
        return ResponseEntity.ok(pokemonList);
    }

    public ResponseEntity<?> getLegendaryPokemons(Integer startId, Integer endId) {
        if (isValidRange(startId, endId)) {
            return ResponseEntity.badRequest().body("Invalid ID range.");
        }

        List<PokemonCardDto> pokemonList = pokemonRepository.getLegendaryPokemons(startId, endId);
        return ResponseEntity.ok(pokemonList);
    }

    public ResponseEntity<?> getMythicalPokemons(Integer startId, Integer endId) {
        if (isValidRange(startId, endId)) {
            return ResponseEntity.badRequest().body("Invalid ID range.");
        }

        List<PokemonCardDto> pokemonList = pokemonRepository.getMythicalPokemons(startId, endId);
        return ResponseEntity.ok(pokemonList);
    }

    private boolean isValidRange(Integer startId, Integer endId) {
        return startId == null || endId == null || startId < 1 || endId > 1025 || startId > endId;
    }

    public ResponseEntity<?> getPokemonById(int pokemonId) {
        if (pokemonId < 1 || pokemonId > 1025) {
            return ResponseEntity.badRequest().body("Invalid Pokémon ID.");
        }

        Optional<String> pokemonOptional = pokemonRepository.findFullPokemonData(pokemonId);
        if (pokemonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pokemonOptional.get());
    }


}
