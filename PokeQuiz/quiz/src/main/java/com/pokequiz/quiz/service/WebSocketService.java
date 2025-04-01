
package com.pokequiz.quiz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokequiz.quiz.dto.DetailedAnswer;
import com.pokequiz.quiz.dto.StatsDTO;
import com.pokequiz.quiz.dto.WsAnswerValidationDTO;
import com.pokequiz.quiz.model.*;
import com.pokequiz.quiz.repository.*;
import jakarta.persistence.Tuple;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class WebSocketService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;
    private final PlayerAnswerRepository playerAnswerRepository;
    private final RoomQuizRepository roomQuizRepository;
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final Map<Long, ScheduledExecutorService> roomSchedulers = new ConcurrentHashMap<>();


    public WebSocketService(RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate, GameService gameService, PlayerAnswerRepository playerAnswerRepository, RoomQuizRepository roomQuizRepository, PlayerRepository playerRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
        this.playerAnswerRepository = playerAnswerRepository;
        this.roomQuizRepository = roomQuizRepository;
        this.playerRepository = playerRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
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

                    // Fetch already used question IDs for this room
                    List<Long> usedQuestionIds = roomQuizRepository.findQuestionIdsByRoomId(roomId);

                    // Fetch a new question that hasn't been used yet
                    Question question = gameService.getRandomQuizExcluding(usedQuestionIds);

                    if (question == null) {
                        System.out.println("No more unique questions available. Stopping broadcast for room " + roomId);
                        stopBroadcasting(roomId);
                        return;
                    }

                    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", question);

                    // Increment round and save question to RoomQuiz
                    room.setCurrentRound(room.getCurrentRound() + 1);
                    RoomQuiz roomQuiz = new RoomQuiz();
                    roomQuiz.setRoom(room);
                    roomQuiz.setQuestion(question);
                    roomQuizRepository.save(roomQuiz);

                    // Create PlayerAnswer entries for all players
                    List<Player> players = playerRepository.findByRoomId(roomId);
                    for (Player player : players) {
                        PlayerAnswer playerAnswer = new PlayerAnswer();
                        playerAnswer.setPlayer(player);
                        playerAnswer.setRoomQuiz(roomQuiz);
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
            }, 0, 30, TimeUnit.SECONDS); // Runs every 3 seconds

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


    public void validateAnswer(WsAnswerValidationDTO answer) {
        Room room = roomRepository.findById(answer.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
//        if (room.isEnded() || !room.isStarted()) {
//            messagingTemplate.convertAndSend("/topic/rooms/" + answer.getRoomId() + "/game", "Game has ended");
//            return;
//        }
        if(answer.getAnswer() == null){
            messagingTemplate.convertAndSend("/topic/rooms/" + answer.getRoomId() + "/game", "Answer cannot be Null");
            return;
        }
        Question question = questionRepository.findById(answer.getQuestionId()).orElseThrow(() -> new RuntimeException("Question not found"));
        Answer correctAnswer = answerRepository.findByQuestion(question);
        Player player = playerRepository.findById(answer.getUserId()).orElseThrow(() -> new RuntimeException("Player not found"));

        // Fetch the RoomQuiz once and reuse it
        RoomQuiz roomQuiz = roomQuizRepository.findByRoomAndQuestion(room, question);
        if (roomQuiz == null) {
            messagingTemplate.convertAndSend("/topic/rooms/" + answer.getRoomId() + "/game", "Question not found");
            return;
        }

        // Retrieve the player's answer (handle case where no answer exists yet)
        PlayerAnswer playerAnswer = playerAnswerRepository.findByPlayerAndRoomQuiz(player, roomQuiz);
        if (playerAnswer == null) {
            messagingTemplate.convertAndSend("/topic/rooms/" + answer.getRoomId() + "/game", "Player answer not found");
            return;
        }

        // Prepare the response DTO
        WsAnswerValidationDTO answerValidationDTO = new WsAnswerValidationDTO();
        answerValidationDTO.setRoomId(answer.getRoomId());
        answerValidationDTO.setUserId(answer.getUserId());
        answerValidationDTO.setQuestionId(answer.getQuestionId());
        answerValidationDTO.setAnswer(answer.getAnswer());

        // Check if the answer is correct
        boolean isCorrect = answer.getAnswer().equalsIgnoreCase(correctAnswer.getCorrectAnswer());

        // Update player's answer and correctness
        playerAnswer.setAnswer(answer.getAnswer());
        playerAnswer.setCorrect(isCorrect);


        // Update player's score if correct
        if (isCorrect) {
            player.setScore(player.getScore() + 10);
            playerRepository.save(player);
            playerAnswerRepository.save(playerAnswer);
            answerValidationDTO.setCorrect(true);
        }

        // Save wrong answer
        playerAnswerRepository.save(playerAnswer);
        answerValidationDTO.setCorrect(isCorrect);
        messagingTemplate.convertAndSend("/topic/rooms/" + answer.getRoomId() + "/game", answerValidationDTO);
    }

    public void sendStats(Long roomId) {
        List<Tuple> results = playerAnswerRepository.findByRoomId(roomId);
        System.out.println(results);

        List<StatsDTO> statsList = results.stream().map(tuple -> {
            Long userId = tuple.get("userId", Long.class);
            String username = tuple.get("username", String.class);
            int totalPoints = (int) tuple.get("totalPoints");

            List<DetailedAnswer> detailedAnswers = new ArrayList<>();

            String detailedAnswersJson = tuple.get("detailedAnswers", String.class);
            if (detailedAnswersJson != null) {
                try {
                    JsonNode jsonNode = new ObjectMapper().readTree(detailedAnswersJson);
                    jsonNode.forEach(answer -> {
                        detailedAnswers.add(new DetailedAnswer(
                                answer.get("questionId").asLong(),
                                answer.get("correctAnswer").asText(),
                                answer.get("userAnswer").asText(),
                                answer.get("isCorrect").asBoolean()
                        ));
                    });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error processing JSON", e);
                }
            }

            return new StatsDTO(userId, username, totalPoints, detailedAnswers);
        }).collect(Collectors.toList());

        // Send the entire stats list as a single message
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/game", statsList);
    }

}
