package com.poke.dex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "damage_relations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DamageRelationId.class)
public class DamageRelation {
    @Id
    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

    @Id
    @ManyToOne
    @JoinColumn(name = "related_type_id")
    private Type relatedType;

    private float timesDamage;
}
