package com.pokeverse.scribble.controller;

import com.pokeverse.scribble.dto.PlayerDTO;
import com.pokeverse.scribble.dto.RoomDTO;
import com.pokeverse.scribble.service.RoomService;
import com.pokeverse.scribble.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoundService roundService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Hello from Scribble service!");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomDTO dto){
        return roomService.createRoom(dto);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody PlayerDTO dto){
        return roomService.joinRoom(dto);
    }

    @GetMapping("/reset")
    public String reset() {
        roundService.reset();
        return "reset success";
    }


}
