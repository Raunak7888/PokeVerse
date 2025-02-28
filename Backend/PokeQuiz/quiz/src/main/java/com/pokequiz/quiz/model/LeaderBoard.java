package com.pokequiz.quiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leaderBoard")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long score;

    private int totalQuestions;

    private int correctAnswers;



    private LocalDateTime created_at;
}
