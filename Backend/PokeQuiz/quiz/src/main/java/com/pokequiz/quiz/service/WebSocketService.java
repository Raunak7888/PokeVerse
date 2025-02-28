
package com.pokequiz.quiz.service;

import com.pokequiz.quiz.model.*;
import com.pokequiz.quiz.repository.PlayerAnswerRepository;
import com.pokequiz.quiz.repository.PlayerRepository;
import com.pokequiz.quiz.repository.RoomQuizRepository;
import com.pokequiz.quiz.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WebSocketService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;
    private final PlayerAnswerRepository playerAnswerRepository;
    private final RoomQuizRepository roomQuizRepository;
    private final PlayerRepository playerRepository;
    private final Map<Long, ScheduledExecutorService> roomSchedulers = new ConcurrentHashMap<>();


    public WebSocketService(RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate, GameService gameService, PlayerAnswerRepository playerAnswerRepository, RoomQuizRepository roomQuizRepository, PlayerRepository playerRepository) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
        this.playerAnswerRepository = playerAnswerRepository;
        this.roomQuizRepository = roomQuizRepository;
        this.playerRepository = playerRepository;
    }


    public void startGame(Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isPresent()) {
            Room r = room.get();
            r.setStarted(true);
            roomRepository.save(r);
            messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", "Game started");
        }
    }

    public void endGame(Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isPresent()) {
            Room r = room.get();
            r.setEnded(true);
            roomRepository.save(r);
            messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", "Game ended");
        }
    }

    public void sendQuestion(Long roomId, Long hostId) {
        // Avoid starting multiple schedulers for the same room
        roomSchedulers.computeIfAbsent(roomId, id -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            System.out.println("Broadcasting started for room " + roomId);

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    Optional<Room> roomOptional = roomRepository.findByIdAndHostId(roomId, hostId);
                    if (roomOptional.isEmpty()) {
                        System.out.println("Room not found. Stopping broadcast for room " + roomId);
                        stopBroadcasting(roomId);
                        return;
                    }

                    Room room = roomOptional.get();

                    if (!room.isStarted() || room.isEnded()) {
                        System.out.println("Room is not active. Stopping broadcast for room " + roomId);
                        stopBroadcasting(roomId);
                        return;
                    }

                    if (room.getCurrentRound() >= room.getMaxRound()) {
                        System.out.println("Max rounds reached. Stopping broadcast for room " + roomId);
                        endGame(roomId);
                        stopBroadcasting(roomId);
                        return;
                    }

                    // Fetch a random question
                    Question question = gameService.getRandomQuiz();
                    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", question);

                    // Increment round and save question to RoomQuiz
                    room.setCurrentRound(room.getCurrentRound() + 1);
                    RoomQuiz roomQuiz = new RoomQuiz();
                    roomQuiz.setRoomId(room);
                    roomQuiz.setQuizId(question);
                    roomQuizRepository.save(roomQuiz);

                    // Create PlayerAnswer entries for all players
                    List<Player> players = playerRepository.findByRoomId(roomId);
                    for (Player player : players) {
                        PlayerAnswer playerAnswer = new PlayerAnswer();
                        playerAnswer.setPlayerId(player);
                        playerAnswer.setQuizId(roomQuiz);
                        playerAnswer.setCorrect(false);
                        playerAnswer.setAnswer(""); // Use empty string or placeholder
                        playerAnswerRepository.save(playerAnswer);
                    }
                    roomRepository.save(room);
                    System.out.println("Sent question to room: " + roomId);

                } catch (Exception e) {
                    System.err.println("Error while broadcasting to room " + roomId);
                    e.printStackTrace();
                    stopBroadcasting(roomId);
                }
            }, 0, 3, TimeUnit.SECONDS); // Runs every 30 seconds

            return scheduler;
        });
    }


    public void stopBroadcasting(Long roomId) {

        ScheduledExecutorService scheduler = roomSchedulers.remove(roomId);
        if (scheduler != null) {
            System.out.println("Stopping broadcast for room " + roomId);
            scheduler.shutdown();

            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow(); // Force shutdown if it doesn't terminate
                    System.out.println("Forced shutdown for room " + roomId);
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while stopping broadcast for room " + roomId);
            }
        }
    }



}
