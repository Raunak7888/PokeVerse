package com.pokequiz.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private String name;
    private int hostId;
    private String hostName;
    private int maxPlayers;
    private int maxRound;
    private List<PlayerDto> players;
}
