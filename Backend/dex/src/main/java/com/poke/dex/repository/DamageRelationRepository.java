package com.poke.dex.repository;

import com.poke.dex.model.DamageRelation;
import com.poke.dex.model.DamageRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DamageRelationRepository extends JpaRepository<DamageRelation, DamageRelationId> {}
