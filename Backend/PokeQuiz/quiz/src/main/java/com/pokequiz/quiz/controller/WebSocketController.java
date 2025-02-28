package com.pokequiz.quiz.controller;

import com.pokequiz.quiz.dto.Message;
import com.pokequiz.quiz.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketService wsService;


    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload Message msg) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", msg);
    }

    @MessageMapping("/start/{roomId}")
    public void startGame(@DestinationVariable Long roomId) {
        wsService.startGame(roomId);
    }

    @MessageMapping("/game/{roomId}/{hostId}")
    public void startGame(@DestinationVariable Long roomId, @DestinationVariable Long hostId) {
        wsService.sendQuestion(roomId, hostId);
    }

}
