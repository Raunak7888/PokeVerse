package com.poke.dex.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeDto {
    private int id;
    private String name;
    private String generation;
    private int type;
    private int relatedType;
    private float timesDamage;
}
