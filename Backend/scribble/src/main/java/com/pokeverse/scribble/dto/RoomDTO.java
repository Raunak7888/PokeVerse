package com.pokeverse.scribble.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDTO {
    private String roomName;
    private Long hostId;
    private String hostUsername;
    private int maxPlayer;
    private int maxRounds;
    private boolean isPublic;
    private String password;
}
