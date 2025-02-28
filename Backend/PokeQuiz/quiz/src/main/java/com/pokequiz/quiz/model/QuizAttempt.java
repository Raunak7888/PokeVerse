package com.pokequiz.quiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private QuizSession sessionId;

    private Long questionId;
    private String selectedAnswer;
    private boolean isCorrect;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
