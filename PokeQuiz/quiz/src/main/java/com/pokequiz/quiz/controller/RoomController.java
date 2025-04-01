package com.pokequiz.quiz.controller;

import com.pokequiz.quiz.dto.*;
import com.pokequiz.quiz.model.*;
import com.pokequiz.quiz.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;


    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomDto roomDto) {
        return roomService.createRoom(roomDto);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinRoom(@RequestParam Long roomId, @RequestBody PlayerDto playerDto) {
        return ResponseEntity.ok(roomService.joinRoom(roomId, playerDto));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoom(@PathVariable Long roomId) {
        Room room = roomService.getRoom(roomId);
        List<Player> players = room.getPlayers();

        Map<String, Object> response = new HashMap<>();
        response.put("room", room);
        response.put("players", players);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/all")
    public ResponseEntity<?> allRooms() {
        return ResponseEntity.ok(roomService.allRooms());
    }


}
