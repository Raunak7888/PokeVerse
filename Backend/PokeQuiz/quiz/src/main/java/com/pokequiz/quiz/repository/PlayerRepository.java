package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {}