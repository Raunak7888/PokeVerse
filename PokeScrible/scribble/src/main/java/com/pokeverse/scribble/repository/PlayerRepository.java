package com.pokeverse.scribble.repository;

import com.pokeverse.scribble.model.Player;
import com.pokeverse.scribble.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {

    List<Player> findByRoom(Room room);
    Player findByRoomIdAndUserId(Long roomId, Long userId);
}
