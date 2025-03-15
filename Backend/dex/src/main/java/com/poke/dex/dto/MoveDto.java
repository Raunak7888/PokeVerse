package com.poke.dex.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveDto {
    private Integer id;
    private String name;
    private String type;
    private Integer power;
    private Integer accuracy;
    private Integer pp;
    private String effect;
    private String damageType;
    private String generation;
}
