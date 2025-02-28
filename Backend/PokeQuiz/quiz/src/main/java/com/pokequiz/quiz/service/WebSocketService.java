package com.pokequiz.quiz.service;

import com.pokequiz.quiz.model.Question;
import com.pokequiz.quiz.model.Room;
import com.pokequiz.quiz.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
    private final Map<Long, ScheduledExecutorService> roomSchedulers = new ConcurrentHashMap<>();


    public WebSocketService(RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
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
                    Question question = gameService.getRandomQuiz();
                    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", question);
                    System.out.println("Sent question to room: " + roomId);
                } catch (Exception e) {
                    System.err.println("Error while broadcasting to room " + roomId + ": " + e.getMessage());
                    stopBroadcasting(roomId);
                }
            }, 0, 30, TimeUnit.SECONDS);

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
