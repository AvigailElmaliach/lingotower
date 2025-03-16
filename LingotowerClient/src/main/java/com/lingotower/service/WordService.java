package com.lingotower.service;

import com.lingotower.model.Word;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class WordService {

    private static final String BASE_URL = "http://localhost:8080/words";
    private RestTemplate restTemplate;

    public WordService() {
        this.restTemplate = new RestTemplate();
    }

    // שליפת כל המילים
    public List<Word> getAllWords() {
        ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
        return response.getBody();
    }

    // שליפת מילה לפי ID
    public Word getWordById(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Word> response = restTemplate.exchange(url, HttpMethod.GET, null, Word.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    // יצירת מילה חדשה
    public Word createWord(Word word) {
        ResponseEntity<Word> response = restTemplate.exchange(
                BASE_URL, HttpMethod.POST, new HttpEntity<>(word), Word.class);
        return response.getBody();  // מחזירים את המילה שנוצרה
    }

    // עדכון מילה לפי ID
    public Word updateWord(Long id, Word wordDetails) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Word> response = restTemplate.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(wordDetails), Word.class);
        return response.getBody();  // מחזירים את המילה המעודכנת
    }

    // מחיקת מילה לפי ID
    public boolean deleteWord(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        return response.getStatusCode() == HttpStatus.NO_CONTENT;  // מחזירים true אם נמחק בהצלחה
    }
}
