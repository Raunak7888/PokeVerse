package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByRoomId(Long roomId);
}