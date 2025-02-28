package com.pokequiz.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String question;
    private String difficulty;
    private String region;

    @Column(name = "quiz_type")
    private String quizType;

    @Column(columnDefinition = "TEXT")
    private String options;  // Store as JSON text

    @JsonIgnore
    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private Answer answer;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public List<String> getOptionsList() {
        try {
            return new ObjectMapper().readValue(options, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Convert List<String> to JSON String before storing
    public void setOptionsList(List<String> optionsList) {
        try {
            this.options = new ObjectMapper().writeValueAsString(optionsList);
        } catch (Exception e) {
            this.options = "[]";  // Default empty array if serialization fails
        }
    }
}
