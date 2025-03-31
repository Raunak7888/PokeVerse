package com.pokequiz.quiz.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId; // Ensure it's "sessionId" NOT "id"

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String quizType;

    @Column(nullable = false)
    private int totalQuestions;

    @CreationTimestamp
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    public enum SessionStatus {
        IN_PROGRESS,
        COMPLETED,
        ABANDONED
    }
}
