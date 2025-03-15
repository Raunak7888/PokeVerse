package com.poke.dex.service;

import com.poke.dex.dto.MoveDto;
import com.poke.dex.dto.OnlyAbilityDto;
import com.poke.dex.dto.PokemonDto;
import com.poke.dex.dto.TypeDto;
import com.poke.dex.model.*;
import com.poke.dex.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ResponseEntity<?> addPokemon(PokemonDto dto) {
        if (pokemonRepository.existsById(dto.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pokemon already exists: " + dto.getName());
        }

        // Fetch Types
        Type type1 = typeRepository.findById(dto.getTypes().getFirst()).orElseThrow();
        Type type2 = (dto.getTypes().size() > 1) ?
                typeRepository.findById(dto.getTypes().get(1)).orElse(null) : null;

        // Create & Save Pokemon Entity
        Pokemon pokemon = new Pokemon(
                dto.getId(),
                dto.getName(),
                dto.getHeight(),
                dto.getWeight(),
                type1,
                type2,
                dto.getBaseExperience(),
                dto.getBaseCatchRate(),
                dto.getBaseHappiness(),
                dto.getSpecies().getFlavorText(),
                dto.getSpecies().getGenus(),
                dto.getSpecies().getHabitat(),
                dto.getSpecies().getColor(),
                dto.getSpecies().getShape(),
                dto.getSpecies().getGeneration(),
                dto.getSpecies().getLegendary(),
                dto.getSpecies().getMythical()
        );
        pokemonRepository.save(pokemon);

        // Save Image
        if (dto.getSpriteUrl() != null) {
            PokemonImage image = new PokemonImage(dto.getId(), pokemon, dto.getSpriteUrl());
            pokemonImageRepository.save(image);
        }
        // Save Abilities
        for (PokemonDto.AbilityDto ability : dto.getAbilities()) {
            Ability abilityEntity = abilityRepository.findById(ability.getId()).orElseThrow();
            PokemonAbility pokemonAbility = new PokemonAbility(pokemon, abilityEntity, ability.getIsHidden());
            pokemonAbilityRepository.save(pokemonAbility);
        }

        // Save Stats
        PokemonStats stats = new PokemonStats(
                dto.getId(),
                pokemon,
                dto.getStats().getHp(),
                dto.getStats().getAttack(),
                dto.getStats().getDefense(),
                dto.getStats().getSpecialAttack(),
                dto.getStats().getSpecialDefense(),
                dto.getStats().getSpeed()
        );
        pokemonStatsRepository.save(stats);

        // Save Breeding Data
        PokemonBreed breed = new PokemonBreed(
                dto.getId(),
                pokemon,
                dto.getBreeding().getEggGroups(),
                dto.getBreeding().getHatchTime(),
                dto.getBreeding().getGenderRatio().getMale(),
                dto.getBreeding().getGenderRatio().getFemale(),
                dto.getGrowthRate()
        );
        pokemonBreedRepository.save(breed);

        PokemonForm pokemonForm = new PokemonForm(
                pokemon,
                dto.getForm().getName(),
                dto.getForm().getFormName(),
                dto.getForm().getIsMega(),
                dto.getForm().getIsGigantamax()
        );
        pokemonFormRepository.save(pokemonForm);
        for (PokemonDto.MoveDto move : dto.getMoves()) {
            Move moveEntity = moveRepository.findById(move.getId()).orElseThrow();
            PokemonMove pokemonMove = new PokemonMove(pokemon, moveEntity, move.getLearnMethod(), move.getLevelLearnedAt());
            pokemonMoveRepository.save(pokemonMove);
        }
        return ResponseEntity.ok("Pokemon added: " + dto.getName());
    }

}
