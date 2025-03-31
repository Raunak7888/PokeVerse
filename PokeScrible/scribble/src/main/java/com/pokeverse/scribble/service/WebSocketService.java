package com.pokeverse.scribble.service;

import com.pokeverse.scribble.dto.MessageDTO;
import com.pokeverse.scribble.model.Room;
import com.pokeverse.scribble.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(Long roomId, MessageDTO messageDto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", messageDto);
    }

    public void startGame(Long roomId,Long hostId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        if (!room.getHostId().equals(hostId)) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/start", "You are not the host!");
        }else {
            room.setStarted(true);
            roomRepository.save(room);
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/start", "The game has started!");
        }
    }
}
