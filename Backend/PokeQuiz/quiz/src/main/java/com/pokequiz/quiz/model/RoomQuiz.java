package com.pokequiz.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Question question;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToMany(mappedBy = "roomQuiz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore // Ignore this field when serializing to JSON
    private List<PlayerAnswer> answers;

    @CreationTimestamp
    private LocalDateTime createdAt;
}