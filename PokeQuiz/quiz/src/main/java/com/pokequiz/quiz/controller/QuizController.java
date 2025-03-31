package com.pokequiz.quiz.controller;

import com.pokequiz.quiz.dto.QuestionDTO;
import com.pokequiz.quiz.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class QuizController {

    private final QuestionService questionService;

    public QuizController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("{\"response\": \"Hello World\"}"); // Correct JSON formatting
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuiz(@RequestBody QuestionDTO questionDTO) {
        if (questionDTO == null) {
            return ResponseEntity.badRequest().body("Question body cannot be null");
        }
        if(questionDTO.getCorrectAnswer() == null) {
            return ResponseEntity.badRequest().body("Correct answer cannot be null");
        }
        if (questionDTO.getOptions() == null || questionDTO.getOptions().size() != 4) {
            return ResponseEntity.badRequest().body("Options should be exactly 4.");
        }

        questionService.addQuiz(questionDTO);
        return ResponseEntity.ok("Quiz added successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllQuizzes() {
        return ResponseEntity.ok(questionService.getAllQuizzes());
    }

    @GetMapping("/difficulty/all")
    public ResponseEntity<?> getAllQuizzesByDifficulty(@RequestParam String difficulty) {
        return ResponseEntity.ok(questionService.getQuizzesByDifficulty(difficulty));
    }

    @GetMapping("/region/all")
    public ResponseEntity<?> getQuizzesByRegion(@RequestParam String region) {
        return ResponseEntity.ok(questionService.getQuizzesByRegion(region));
    }

    @GetMapping("/quiztype/all")
    public ResponseEntity<?> getQuizzesByQuizType(@RequestParam String quizType) {
        return ResponseEntity.ok(questionService.getQuizzesByQuizType(quizType));
    }

    @GetMapping("/region/difficulty/quizType/all")
    public ResponseEntity<?> getQuizzesByRegionAndDifficulty(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String quizType) {
        return ResponseEntity.ok(questionService.getQuizzesByRegionAndDifficulty(region, difficulty,quizType));
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuizzes(@RequestParam int limit) {
        return ResponseEntity.ok(questionService.getRandomQuizzes(limit));
    }

    @GetMapping("/random/difficulty/region/quizType")
    public ResponseEntity<?> getRandomQuizzesAsPerDifficultyAndRegion(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String quizType,
            @RequestParam int limit) {
        return ResponseEntity.ok(questionService.getRandomQuizzesAsPerDifficultyAndRegion(region, difficulty, quizType, limit));
    }
}