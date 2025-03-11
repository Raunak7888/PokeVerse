package com.poke.dex.service;

import com.poke.dex.dto.TypeDto;
import com.poke.dex.model.DamageRelation;
import com.poke.dex.model.Type;
import com.poke.dex.repository.DamageRelationRepository;
import com.poke.dex.repository.TypeRepository;
import org.springframework.stereotype.Service;

@Service
public class PokemonService {

    private final TypeRepository typeRepository;
    private final DamageRelationRepository damageRepository;

    public PokemonService(TypeRepository typeRepository, DamageRelationRepository damageRepository) {
        this.typeRepository = typeRepository;
        this.damageRepository = damageRepository;
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
}
