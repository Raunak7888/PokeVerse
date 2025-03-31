package com.pokeverse.scribble.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDTO {
    private Long roomId;
    private Long playerId;
    private String username;
    private String password;
}
