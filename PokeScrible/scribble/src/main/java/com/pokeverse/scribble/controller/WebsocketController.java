package com.pokeverse.scribble.controller;

import com.pokeverse.scribble.dto.AnswerDTO;
import com.pokeverse.scribble.dto.MessageDTO;
import com.pokeverse.scribble.dto.RoundDTO;
import com.pokeverse.scribble.service.RoundService;
import com.pokeverse.scribble.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class WebsocketController {

    private final WebSocketService webSocketService;
    private final RoundService roundService;

    @MessageMapping("/chat/{roomId}")
    public void chat(@DestinationVariable Long roomId, MessageDTO messageDto) {
        webSocketService.sendMessage(roomId,messageDto);
    }

    @MessageMapping("/rooms/{roomId}/{hostId}/start")
    public void startGame(@DestinationVariable Long roomId,@DestinationVariable Long hostId) {
        webSocketService.startGame(roomId,hostId);
    }

    @MessageMapping("/game/round/{roomId}")
    public void startQuestions(@DestinationVariable Long roomId) {
        roundService.startRounds(roomId);
    }

    @MessageMapping("/game/round/{roomId}/to/guess")
    public void setWhatToGuess(@DestinationVariable Long roomId,@Payload RoundDTO dto) {
        roundService.setWhatToGuess(roomId,dto);
    }
    @MessageMapping("/game/round/{roomId}/get/guess")
    public void getWhatToGuess(@DestinationVariable Long roomId) {
        roundService.getWhatToGuess(roomId);
    }

    @MessageMapping("/game/round/{roomId}/answer")
    public void answerQuestion(@DestinationVariable Long roomId,@Payload AnswerDTO dto) {
        roundService.answerValidate(roomId,dto);
    }


}
