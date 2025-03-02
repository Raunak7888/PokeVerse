package com.pokequiz.quiz.service;

import com.pokequiz.quiz.model.*;
import com.pokequiz.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAnalysisService {

    private final QuizAnalysisRepository quizAnalysisRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // Trigger analysis when quiz session is marked as COMPLETED
    @Transactional
    public QuizAnalysis analyzeQuiz(Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz session not found: " + sessionId));

        // Ensure analysis happens only for COMPLETED sessions
        if (session.getStatus() != QuizSession.SessionStatus.COMPLETED) {
            throw new IllegalStateException("Quiz session is not completed yet!");
        }

        // Fetch all attempts related to the session
        List<QuizAttempt> attempts = quizAttemptRepository.findBySessionId(session);
        if (attempts.isEmpty()) {
            throw new IllegalStateException("No attempts found for session: " + sessionId);
        }

        // Calculate stats
        int correctAnswers = (int) attempts.stream().filter(QuizAttempt::isCorrect).count();
        int wrongAnswers = attempts.size() - correctAnswers;
        double accuracy = (double) correctAnswers / attempts.size() * 100;

        Duration totalDuration = Duration.between(session.getStartTime(), session.getEndTime());
        Duration averageTimePerQuestion = totalDuration.dividedBy(attempts.size());
        Duration fastestAnswerTime = attempts.stream()
                .map(attempt -> Duration.between(attempt.getStartTime(), attempt.getEndTime()))
                .min(Duration::compareTo)
                .orElse(Duration.ZERO);

        Duration slowestAnswerTime = attempts.stream()
                .map(attempt -> Duration.between(attempt.getStartTime(), attempt.getEndTime()))
                .max(Duration::compareTo)
                .orElse(Duration.ZERO);

        String answerSpeedRating = calculateAnswerSpeed(averageTimePerQuestion);
        String performanceRating = calculatePerformanceRating(accuracy);

        // Map question analysis
        // Step 1: Collect all question IDs from attempts
        List<Long> questionIds = attempts.stream()
                .map(QuizAttempt::getQuestionId)
                .distinct()
                .toList();

// Step 2: Fetch all questions in a single database call
        Map<Long, Question> questionMap = questionRepository.findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(Question::getId, question -> question));

// Step 3: Build the QuestionAnalysis list efficiently
        List<QuizAnalysis.QuestionAnalysis> questionAnalysisList = attempts.stream()
                .map(attempt -> {
                    Question question = questionMap.get(attempt.getQuestionId());
                    Optional<Answer> answer = answerRepository.findById(question.getId());
                    if (answer.isEmpty()){
                        throw new IllegalStateException("Answer not found for question: " + question.getId());
                    }
                    return new QuizAnalysis.QuestionAnalysis(
                            attempt.getQuestionId(),
                            question.getQuestion(),
                            question.getDifficulty(),
                            question.getRegion(),
                            question.getQuizType(),
                            attempt.getSelectedAnswer(),
                            question.getOptionsList(),
                            answer.get().getCorrectAnswer(),
                            attempt.isCorrect(),
                            Duration.between(attempt.getStartTime(), attempt.getEndTime()).toMillis()
                    );
                })
                .collect(Collectors.toList());


        // Create and save QuizAnalysis
        QuizAnalysis analysis = QuizAnalysis.builder()
                .sessionId(sessionId)
                .userId(session.getUserId())
                .quizType(session.getQuizType())
                .difficulty(session.getDifficulty())
                .region(session.getRegion())
                .totalQuestions(attempts.size())
                .correctAnswers(correctAnswers)
                .wrongAnswers(wrongAnswers)
                .accuracy(accuracy)
                .totalDuration(totalDuration.toMillis())
                .averageTimePerQuestion(averageTimePerQuestion.toMillis())
                .fastestAnswerTime(fastestAnswerTime.toMillis())
                .slowestAnswerTime(slowestAnswerTime.toMillis())
                .answerSpeedRating(answerSpeedRating)
                .performanceRating(performanceRating)
                .questionAnalysis(questionAnalysisList)
                .build();

        return quizAnalysisRepository.save(analysis);
    }

    // Helper: Calculate speed rating
    private String calculateAnswerSpeed(Duration avgTime) {
        long seconds = avgTime.getSeconds();
        if (seconds <= 30) return "Fast";
        else if (seconds <= 90) return "Average";
        return "Slow";
    }

    // Helper: Calculate performance rating
    private String calculatePerformanceRating(double accuracy) {
        if (accuracy >= 90) return "Ace";
        else if (accuracy >= 70) return "Pro";
        return "Rookie";
    }

    // Retrieve analysis by sessionId
    public Optional<QuizAnalysis> getAnalysisBySessionId(Long sessionId) {
        return quizAnalysisRepository.findBySessionId(sessionId);
    }

    // Get all analyses for a user
    public List<QuizAnalysis> getUserAnalyses(Long userId) {
        return quizAnalysisRepository.findAllByUserId(userId);
    }
}
