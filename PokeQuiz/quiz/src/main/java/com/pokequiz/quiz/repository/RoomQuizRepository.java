package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Question;
import com.pokequiz.quiz.model.Room;
import com.pokequiz.quiz.model.RoomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomQuizRepository extends JpaRepository<RoomQuiz, Long> {
    RoomQuiz findByRoomAndQuestion(Room room, Question question);

    @Query("SELECT rq.question.id FROM RoomQuiz rq WHERE rq.room.id = :roomId")
    List<Long> findQuestionIdsByRoomId(@Param("roomId") Long roomId);
}
