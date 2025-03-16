package com.lingotower.service;

import com.lingotower.model.Quiz;
import com.lingotower.data.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lingotower.controller.QuizController;


import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }
    public Optional<Quiz> updateQuiz(Long id, Quiz updatedQuiz) {
        return quizRepository.findById(id).map(existingQuiz -> {
            existingQuiz.setName(updatedQuiz.getName());
            existingQuiz.setCategory(updatedQuiz.getCategory());
            existingQuiz.setDifficulty(updatedQuiz.getDifficulty());
            existingQuiz.setAdminByCreated(updatedQuiz.getAdminByCreated());
            return quizRepository.save(existingQuiz);
        });
    }
}
