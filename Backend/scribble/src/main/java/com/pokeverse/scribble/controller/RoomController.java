package com.pokeverse.scribble.controller;

import com.pokeverse.scribble.dto.PlayerDTO;
import com.pokeverse.scribble.dto.RoomDTO;
import com.pokeverse.scribble.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomDTO dto){
        return roomService.createRoom(dto);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody PlayerDTO dto){
        return roomService.joinRoom(dto);
    }


}
