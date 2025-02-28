package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.PlayerAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, Long> {}

