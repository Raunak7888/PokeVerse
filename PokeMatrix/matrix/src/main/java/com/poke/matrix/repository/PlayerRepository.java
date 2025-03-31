package com.poke.matrix.repository;

import com.poke.matrix.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {

    Player findByUserId(Long userId);
}
