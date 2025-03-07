package com.pokeverse.scribble.service;

import com.pokeverse.scribble.components.NonDuplicateQueue;
import com.pokeverse.scribble.dto.AnswerDTO;
import com.pokeverse.scribble.dto.RoundDTO;
import com.pokeverse.scribble.model.Player;
import com.pokeverse.scribble.model.Room;
import com.pokeverse.scribble.repository.PlayerRepository;
import com.pokeverse.scribble.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoundService {

    private static final Logger logger = LoggerFactory.getLogger(RoundService.class);

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, ScheduledExecutorService> roomSchedulers = new ConcurrentHashMap<>();
    private final Map<Long, NonDuplicateQueue<Player>> roomPlayersQueue = new ConcurrentHashMap<>();
    private final Map<Long, RoundDTO> roundDTOs = new ConcurrentHashMap<>();

    public void startRounds(Long roomId) {
        roomSchedulers.computeIfAbsent(roomId, id -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            logger.info("Broadcasting started for room {}", roomId);

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    Optional<Room> roomOptional = roomRepository.findById(roomId);
                    if (roomOptional.isEmpty()) {
                        sendMessage(roomId, "Room not found");
                        stopBroadcasting(roomId);
                        return;
                    }

                    Room room = roomOptional.get();

                    if (!room.isStarted() || room.isEnded()) {
                        sendMessage(roomId, "Game not started or already ended");
                        stopBroadcasting(roomId);
                        return;
                    }

                    if (room.getCurrentRound() >= room.getMaxRounds()) {
                        endGame(room);
                        stopBroadcasting(roomId);
                        return;
                    }

                    roomPlayersQueue.putIfAbsent(roomId, new NonDuplicateQueue<>());
                    NonDuplicateQueue<Player> playersQueue = roomPlayersQueue.get(roomId);
                    List<Player> players = playerRepository.findByRoom(room);

                    if (players.isEmpty()) {
                        sendMessage(roomId, "No players available.");
                        stopBroadcasting(roomId);
                        return;
                    }

                    // If queue is empty, refill and increment round
                    if (playersQueue.isEmpty()) {
                        room.setCurrentRound(room.getCurrentRound() + 1);
                        playersQueue.enqueue(players);
                        roomRepository.save(room);
                        logger.info("Starting round {} for room {}", room.getCurrentRound(), roomId);

                        if (room.getCurrentRound() >= room.getMaxRounds()) {
                            endGame(room);
                            stopBroadcasting(roomId);
                            return;
                        }
                    }

                    // Assign the next player as the drawer
                    Player currentPlayer = playersQueue.dequeue();
                    if (currentPlayer == null) {
                        sendMessage(roomId, "No player to draw.");
                        return;
                    }

                    room.setDrawer(currentPlayer);
                    roomRepository.save(room);

                    sendMessage(roomId, "Round " + room.getCurrentRound() + " started. " +
                            currentPlayer.getUsername() + " is guessing what to draw.");

                    // Delayed message to indicate the drawing phase
                    scheduleDelayedMessage(roomId, room.getCurrentRound(), currentPlayer);

                } catch (Exception e) {
                    logger.error("Error while broadcasting to room {}", roomId, e);
                    stopBroadcasting(roomId);
                }
            }, 0, 70, TimeUnit.SECONDS);

            return scheduler;
        });
    }

    private void scheduleDelayedMessage(Long roomId, int currentRound, Player currentPlayer) {
        ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();
        delayedExecutor.schedule(() -> {
            try {
                sendMessage(roomId, "Round " + currentRound + " started. " +
                        currentPlayer.getUsername() + " is drawing.");
            } catch (Exception e) {
                logger.error("Error during delayed message for room {}", roomId, e);
            } finally {
                delayedExecutor.shutdown();
            }
        }, 10, TimeUnit.SECONDS); // 10-second delay
    }


    private void endGame(Room room) {
        room.setEnded(true);
        roomRepository.save(room);
        roomPlayersQueue.remove(room.getId()); // Cleanup
        roundDTOs.remove(room.getId()); // Cleanup
        sendMessage(room.getId(), "Game ended");
        getStats(room.getId());
        logger.info("Game ended for room {}", room.getId());
    }

    public void stopBroadcasting(Long roomId) {
        ScheduledExecutorService scheduler = roomSchedulers.remove(roomId);
        if (scheduler != null) {
            logger.info("Stopping broadcast for room {}", roomId);
            scheduler.shutdown();

            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    logger.warn("Forced shutdown for room {}", roomId);
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                logger.error("Interrupted while stopping broadcast for room {}", roomId);
            }
        }
    }

    private void sendMessage(Long roomId, String message) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", Map.of("message", message));
    }

    public void setWhatToGuess(Long roomId, RoundDTO dto) {
        if (dto == null || !isValid(dto)) {
            sendMessage(roomId, "Invalid payload.");
            return; // Early exit on invalid input
        }

        roundDTOs.merge(roomId, dto, (oldDto, newDto) -> {
            if (isValid(newDto)) {
                logger.info("Updating word to guess for room {}: Old: {}, New: {}", roomId, oldDto, newDto);
                return newDto; // Update with new if valid
            }
            return oldDto; // Retain old if new is invalid
        });
        int lengthOfWord = roundDTOs.get(roomId).getToGuess().length();

        // Send the length as a Map
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game",
                Map.of("lengthOfWord", lengthOfWord));
        logger.info("Set word to guess for room {}: {}",roundDTOs.get(roomId).getToGuess(),roundDTOs.get(roomId));
    }

    private boolean isValid(RoundDTO dto) {
        return dto.getToGuess() != null && !dto.getToGuess().isEmpty();
    }

    public void getWhatToGuess(Long roomId) {
        if (!roundDTOs.containsKey(roomId)) {
            sendMessage(roomId, "Round not found for room: " + roomId);
            return;
        }

        String toGuess = roundDTOs.get(roomId).getToGuess();
        if (toGuess == null || toGuess.isEmpty()) {
            sendMessage(roomId, "No word to guess.");
            return;
        }

        // Get a random character and its position
        Random random = new Random();
        int randomIndex = random.nextInt(toGuess.length());
        char randomChar = toGuess.charAt(randomIndex);

        // Create a dynamic object using a Map
        Map<String, Object> hintObject = new HashMap<>();
        hintObject.put("roomId", roomId);
        hintObject.put("hintChar", randomChar);
        hintObject.put("position", randomIndex);

        // Send the object
        sendMessage3(roomId, hintObject);
    }

    private void sendMessage3(Long roomId, Map<String, Object> hintObject) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game" + hintObject);
    }


    public void answerValidate(Long roomId, AnswerDTO dto) {
        if (dto == null) {
            sendMessage(roomId, "Invalid payload.");
            return;
        }

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found."));

        if (room.isEnded()) {
            sendMessage(roomId, "Game has ended.");
            return;
        }

        if (room.getDrawer() == null || Objects.equals(room.getDrawer().getUserId(), dto.getUserId())) {
            sendMessage(roomId, "You are the drawer.");
            return;
        }

        Player player = playerRepository.findByRoomIdAndUserId(roomId, dto.getUserId());
        if (player == null) {
            sendMessage(roomId, "Player not found.");
            return;
        }

        RoundDTO roundDTO = roundDTOs.get(roomId);
        if (roundDTO == null || roundDTO.getToGuess() == null) {
            sendMessage(roomId, "No active round or word to guess.");
            return;
        }

        // Validate the answer
        if (roundDTO.getToGuess().equalsIgnoreCase(dto.getAnswer())) {
            sendMessage(roomId, player.getUsername() + " guessed the correct answer!");

            // Calculate points (between 0 and 60)
            int points = Math.max(0, Math.min(60, 60 - dto.getTime()));
            player.setScore(player.getScore() + points);
            playerRepository.save(player);

            new AnswerDTO(dto.getUserId(), dto.getAnswer(), points);
        } else {
            sendMessage(roomId, player.getUsername() + " guessed: " + dto.getAnswer());
            new AnswerDTO(dto.getUserId(), dto.getAnswer(), 0);
        }
    }

    public void reset(){
        Room room = roomRepository.findById(1L).orElseThrow();
        room.setEnded(false);
        room.setCurrentRound(0);
        roomRepository.save(room);
    }


    public void getStats(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        List<Player> players = playerRepository.findByRoom(room);
        if (players.isEmpty()) {
            sendMessage(roomId, "No players found.");
            return;
        }

        List<Map<String, Object>> playersStats = new ArrayList<>();

        for (Player player : players) {
            Map<String, Object> playerMap = new HashMap<>();
            playerMap.put("userId", player.getUserId());
            playerMap.put("username", player.getUsername());
            playerMap.put("score", player.getScore());
            playersStats.add(playerMap);
        }

        sendMessage2(roomId, playersStats);
    }

    private void sendMessage2(Long roomId, List<Map<String, Object>> playersStats) {
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", playersStats);
    }

}
