package com.lingotower.service;


import com.lingotower.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserService {

    private static final String BASE_URL = "http://localhost:8080/users";
    private RestTemplate restTemplate;

    public UserService() {
        this.restTemplate = new RestTemplate();
    }

    public List<User> getAllUsers() {
        ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
        return response.getBody();
    }

    public User getUserById(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, null, User.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public User createUser(User user) {
        ResponseEntity<User> response = restTemplate.exchange(
                BASE_URL, HttpMethod.POST, new HttpEntity<>(user), User.class);
        return response.getBody();
    }

    public boolean deleteUser(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        return response.getStatusCode() == HttpStatus.NO_CONTENT;
    }

    public Double getUserLearningProgress(Long userId) {
        String url = BASE_URL + "/" + userId + "/progress";
        ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.GET, null, Double.class);
        return response.getBody();
    }

    public boolean addLearnedWord(Long userId, Long wordId) {
        String url = BASE_URL + "/" + userId + "/learn-word/" + wordId;
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, null, Void.class);
        return response.getStatusCode() == HttpStatus.OK;
    }
}
