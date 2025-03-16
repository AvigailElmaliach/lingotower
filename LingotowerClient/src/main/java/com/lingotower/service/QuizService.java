package com.lingotower.service;

import com.lingotower.model.Quiz;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class QuizService {

    private static final String BASE_URL = "http://localhost:8080/quizzes";
    private RestTemplate restTemplate;

    public QuizService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Quiz> getAllQuizzes() {
        ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
        return response.getBody();
    }

    public Quiz getQuizById(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Quiz> response = restTemplate.exchange(url, HttpMethod.GET, null, Quiz.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public Quiz createQuiz(Quiz quiz) {
        ResponseEntity<Quiz> response = restTemplate.exchange(
                BASE_URL, HttpMethod.POST, new HttpEntity<>(quiz), Quiz.class);
        return response.getBody();
    }

  
}