package com.lingotower.controller;

import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.service.CompletionPracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/practice/completion") 
public class CompletionPracticeController {

    @Autowired
    private CompletionPracticeService completionPracticeService;

    @GetMapping("/generate") 
    public ResponseEntity<Question> generateCompletionPractice(
            @RequestParam String category,
            @RequestParam Difficulty difficulty) { 
        Optional<Question> practiceQuestion = completionPracticeService.generateCompletionPractice(category, difficulty);
        return practiceQuestion.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}