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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", message);
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

        logger.info("Set word to guess for room {}: {}", roomId, dto);
    }

    private boolean isValid(RoundDTO dto) {
        return dto.getToGuess() != null && !dto.getToGuess().isEmpty();
    }

    public RoundDTO getWhatToGuess(Long roomId) {
        return roundDTOs.get(roomId);
    }

    public AnswerDTO answerValidate(Long roomId, AnswerDTO dto) {
        if (dto == null) {
            sendMessage(roomId, "Invalid payload.");
            return null;
        }

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found."));

        if (room.isEnded()) {
            sendMessage(roomId, "Game has ended.");
            return null;
        }

        if (room.getDrawer() == null || Objects.equals(room.getDrawer().getUserId(), dto.getUserId())) {
            sendMessage(roomId, "You are the drawer.");
            return null;
        }

        Player player = playerRepository.findByRoomIdAndUserId(roomId, dto.getUserId());
        if (player == null) {
            sendMessage(roomId, "Player not found.");
            return null;
        }

        RoundDTO roundDTO = roundDTOs.get(roomId);
        if (roundDTO == null || roundDTO.getToGuess() == null) {
            sendMessage(roomId, "No active round or word to guess.");
            return null;
        }

        // Validate the answer
        if (roundDTO.getToGuess().equalsIgnoreCase(dto.getAnswer())) {
            sendMessage(roomId, player.getUsername() + " guessed the correct answer!");

            // Calculate points (between 0 and 60)
            int points = Math.max(0, Math.min(60, 60 - dto.getTime()));
            player.setScore(player.getScore() + points);
            playerRepository.save(player);

            return new AnswerDTO(dto.getUserId(), dto.getAnswer(), points);
        } else {
            sendMessage(roomId, player.getUsername() + " guessed: " + dto.getAnswer());
            return new AnswerDTO(dto.getUserId(), dto.getAnswer(), 0); // Return with 0 points for incorrect guess
        }
    }




}
