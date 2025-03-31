package com.pokequiz.quiz.service;

import com.pokequiz.quiz.dto.QuestionDTO;
import com.pokequiz.quiz.model.Answer;
import com.pokequiz.quiz.model.Question;
import com.pokequiz.quiz.repository.AnswerRepository;
import com.pokequiz.quiz.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional
    public void addQuiz(QuestionDTO questionDTO) {
        Optional<Question> existingQuestion = questionRepository.findByQuestion(questionDTO.getQuestion());
        if (existingQuestion.isPresent()) {
            Answer existingAnswer = answerRepository.findByQuestion(existingQuestion.get());
            if (existingAnswer != null &&
                    Objects.equals(existingAnswer.getCorrectAnswer(), questionDTO.getCorrectAnswer())) {
                throw new RuntimeException("Question already exists with the same answer!");
            }
        }
        Question question = new Question();
        question.setQuestion(questionDTO.getQuestion());
        question.setDifficulty(questionDTO.getDifficulty());
        question.setRegion(questionDTO.getRegion());
        question.setQuizType(questionDTO.getQuizType());
        question.setOptionsList(questionDTO.getOptions());
        question = questionRepository.save(question);
        Answer answer = new Answer();
        answer.setCorrectAnswer(questionDTO.getCorrectAnswer());
        answer.setQuestion(question);
        answerRepository.save(answer);
    }


    public List<QuestionDTO> getAllQuizzes() {
        return convertToDTO(questionRepository.findAll());
    }

    public List<QuestionDTO> getQuizzesByDifficulty(String difficulty) {
        return convertToDTO(questionRepository.findByDifficulty(difficulty));
    }

    public List<QuestionDTO> getQuizzesByRegion(String region) {
        return convertToDTO(questionRepository.findByRegion(region));
    }

    public List<QuestionDTO> getQuizzesByRegionAndDifficulty(String region,String difficulty, String quizType) {
        return convertToDTO(questionRepository.findByDifficultyAndRegion(region, difficulty, quizType));
    }

    public List<QuestionDTO> getRandomQuizzes(int limit) {
        return convertToDTO(questionRepository.findRandomQuestions(limit));
    }

    public List<QuestionDTO> getRandomQuizzesAsPerDifficultyAndRegion(String region, String difficulty, String quizType, int limit) {
        return convertToDTO(questionRepository.findRandomQuestionsAsPerDifficultyAndRegion(region, difficulty, quizType, limit));
    }

    private List<QuestionDTO> convertToDTO(List<Question> questions) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        List<Answer> answers = answerRepository.findByQuestionIn(questions);
        Map<Long, String> answerMap = answers.stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), Answer::getCorrectAnswer));
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setQuestion(question.getQuestion());
            questionDTO.setDifficulty(question.getDifficulty());
            questionDTO.setRegion(question.getRegion());
            questionDTO.setQuizType(question.getQuizType());
            questionDTO.setOptions(question.getOptionsList());
            questionDTO.setCorrectAnswer(answerMap.get(question.getId()));
            questionDTOs.add(questionDTO);
        }
        return questionDTOs;
    }

    public List<QuestionDTO> getQuizzesByQuizType(String quizType) {
        return convertToDTO(questionRepository.findByQuizType(quizType));
    }
}
