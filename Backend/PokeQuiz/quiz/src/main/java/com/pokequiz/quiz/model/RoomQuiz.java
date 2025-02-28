package com.pokequiz.quiz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "room_quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Question quizId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room roomId;

    @OneToMany(mappedBy = "quizId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerAnswer> answers;

    @CreationTimestamp
    private LocalDateTime createdAt;
}