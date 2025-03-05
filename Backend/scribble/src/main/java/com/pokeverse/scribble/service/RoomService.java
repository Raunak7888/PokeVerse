package com.pokeverse.scribble.service;

import com.pokeverse.scribble.dto.PlayerDTO;
import com.pokeverse.scribble.dto.RoomDTO;
import com.pokeverse.scribble.model.Player;
import com.pokeverse.scribble.model.Room;
import com.pokeverse.scribble.repository.PlayerRepository;
import com.pokeverse.scribble.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;

    /**
     * Creates a new room and adds the host as the first player.
     */
    public ResponseEntity<?> createRoom(RoomDTO dto) {
        if (dto.getRoomName() == null || dto.getRoomName().isEmpty()) {
            return ResponseEntity.badRequest().body("dto.getRoomName(): "+dto.getRoomName() +" Room name is required");
        }

        Room room = new Room();
        room.setRoomName(dto.getRoomName());
        room.setHostId(dto.getHostId());
        room.setMaxPlayer(dto.getMaxPlayer());
        room.setMaxRounds(dto.getMaxRounds());
        room.setPublic(dto.isPublic());

        // Ensure password is provided for private rooms
        if (!dto.isPublic()) {
            if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required for private rooms");
            }
            room.setPassword(dto.getPassword());
        }

        // Save the room first
        Room savedRoom = roomRepository.save(room);

        // Add the host as the first player
        Player hostPlayer = new Player();
        hostPlayer.setUserId(dto.getHostId());
        hostPlayer.setUsername(dto.getHostUsername());
        hostPlayer.setRoom(savedRoom);
        hostPlayer.setDrawer(true); // Host could be the first drawer
        hostPlayer.setStatus("ACTIVE");
        playerRepository.save(hostPlayer);

        // Update current drawer in the room
        savedRoom.setDrawer(hostPlayer);
        roomRepository.save(savedRoom);

        return ResponseEntity.ok(savedRoom);
    }

    /**
     * Allows a player to join an existing room.
     */
    public ResponseEntity<?> joinRoom(PlayerDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId()).orElse(null);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        // Check if the room is full
        if (room.getPlayers().size() >= room.getMaxPlayer()) {
            return ResponseEntity.badRequest().body("Room is full");
        }

        // Handle private room access
        if (!room.isPublic()) {
            if (dto.getPassword() == null || !dto.getPassword().equals(room.getPassword())) {
                return ResponseEntity.badRequest().body("Incorrect or missing password for private room");
            }
        }

        // Create a new player and save to the room
        Player player = new Player();
        player.setUserId(dto.getPlayerId());
        player.setUsername(dto.getUsername());
        player.setRoom(room);
        player.setDrawer(false);
        player.setStatus("ACTIVE");

        playerRepository.save(player);

        return ResponseEntity.ok(player);
    }
}
