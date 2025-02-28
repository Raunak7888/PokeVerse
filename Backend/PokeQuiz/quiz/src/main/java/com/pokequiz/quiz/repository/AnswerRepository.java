package com.pokequiz.quiz.repository;

import com.pokequiz.quiz.model.Answer;
import com.pokequiz.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Answer findByQuestion(Question question);
    List<Answer> findByQuestionIn(List<Question> questions);
}
