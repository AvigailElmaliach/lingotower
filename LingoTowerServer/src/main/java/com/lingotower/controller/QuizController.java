package com.lingotower.controller;

import com.lingotower.dto.QuizResponse;
import com.lingotower.dto.quiz.QuestionDTO;
//import com.lingotower.dto.quiz.QuizRequest;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Quiz;
import com.lingotower.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.desktop.QuitResponse;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz newQuiz = quizService.createQuiz(quiz);
        return ResponseEntity.ok(newQuiz);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz updatedQuiz) {
        Optional<Quiz> quiz = quizService.updateQuiz(id, updatedQuiz);
        return quiz.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    



        @GetMapping("/generate")
        public ResponseEntity<List<QuestionDTO>> generateQuiz(
                @RequestParam Long categoryId,
                @RequestParam Difficulty difficulty,
                Principal principal) {
            
            // קבלת שם המשתמש מתוך ה-Principal (המשתמש המחובר)
            String username = principal.getName();
            
            // קריאה לפונקציה generateQuiz של ה-QuizService
            List<QuestionDTO> quiz = quizService.generateQuiz(categoryId, difficulty, username);
            
            // החזרת רשימת השאלות בתגובה
            return ResponseEntity.ok(quiz);
        }
    }



