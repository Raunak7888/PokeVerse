package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByStarted(boolean b);

    Optional<Room> findByNameAndHostId(String name, Long hostId);

    Optional<Room> findByIdAndHostId(Long id, Long hostId);
}