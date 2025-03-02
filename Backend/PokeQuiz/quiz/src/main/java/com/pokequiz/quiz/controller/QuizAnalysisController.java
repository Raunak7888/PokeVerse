package com.pokequiz.quiz.controller;

import com.pokequiz.quiz.model.QuizAnalysis;
import com.pokequiz.quiz.service.QuizAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequiredArgsConstructor
public class QuizAnalysisController {

    private final QuizAnalysisService quizAnalysisService;

    @PostMapping("/{sessionId}")
    public ResponseEntity<?> analyzeQuiz(@PathVariable Long sessionId) {
        QuizAnalysis analysis = quizAnalysisService.analyzeQuiz(sessionId);
        return analysis != null ? ResponseEntity.ok(analysis) : ResponseEntity.badRequest().body("Failed to analyze quiz.");
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getAnalysis(@PathVariable Long sessionId) {
        return quizAnalysisService.getAnalysisBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAnalyses(@PathVariable Long userId) {
        List<QuizAnalysis> analyses = quizAnalysisService.getUserAnalyses(userId);
        return ResponseEntity.ok(analyses);
    }
}
