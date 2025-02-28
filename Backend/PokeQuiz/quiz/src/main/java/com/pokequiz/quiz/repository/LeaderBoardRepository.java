package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.LeaderBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderBoardRepository extends JpaRepository<LeaderBoard, Long> {
    List<LeaderBoard> findTop10ByOrderByScoreDesc();
}

