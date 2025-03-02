package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Player;
import com.pokequiz.quiz.model.PlayerAnswer;
import com.pokequiz.quiz.model.RoomQuiz;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, Long> {
    PlayerAnswer findByPlayerAndRoomQuiz(Player player, RoomQuiz roomQuiz);
    @Query(value = """
    SELECT 
        p.user_id AS userId,
        p.name AS username,
        p.score AS totalPoints,
        jsonb_agg(
            jsonb_build_object(
                'questionId', pa.quiz_id,
                'correctAnswer', a.correct_answer,
                'userAnswer', pa.answer,
                'isCorrect', pa.correct
            )
        ) AS detailedAnswers
    FROM players p
    JOIN player_answers pa ON p.id = pa.player_id
    JOIN room_quizzes rq ON pa.quiz_id = rq.quiz_id
    JOIN answers a ON pa.quiz_id = a.question_id
    WHERE p.room_id = :roomId
    GROUP BY p.user_id, p.name, p.score
    ORDER BY p.user_id
""", nativeQuery = true)
    List<Tuple> findByRoomId(@Param("roomId") Long roomId);


}

