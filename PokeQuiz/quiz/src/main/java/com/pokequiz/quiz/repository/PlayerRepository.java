package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p WHERE p.room.id = :roomId")
    List<Player> findByRoomId(@Param("roomId") Long roomId);

}