package com.lingotower.controller;

import com.lingotower.model.ExampleSentence;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/sentences")
public class HuggingFaceSentenceGenerator {

    private static final String HF_API_TOKEN = ""; 
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";
    private final WordRepository wordRepository;
    private final ExampleSentenceRepository sentenceRepository;

    public HuggingFaceSentenceGenerator(WordRepository wordRepository, ExampleSentenceRepository sentenceRepository) {
        this.wordRepository = wordRepository;
        this.sentenceRepository = sentenceRepository;
    }

    @GetMapping("/generate-sentences")
    public ResponseEntity<Map<String, List<String>>> generateSentences() {
        List<Word> words = wordRepository.findAll(); // שליפת כל המילים מהדאטהבייס
        Map<String, List<String>> wordSentencesMap = new HashMap<>();

        for (Word word : words) {
            String keyword = word.getWord();
            List<String> sentences = fetchExampleSentences(keyword);

            if (!sentences.isEmpty()) {
                wordSentencesMap.put(keyword, sentences);

                // שמירת המשפטים שנוצרו
                for (String sentence : sentences) {
                    ExampleSentence exampleSentence = new ExampleSentence(sentence, word);
                    sentenceRepository.save(exampleSentence);
                }
            }
        }

        return ResponseEntity.ok(wordSentencesMap);
    }

    private List<String> fetchExampleSentences(String word) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + HF_API_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // בניית ה-prompt
        String prompt = "Create two example sentences using the word: '" + word + "'.";

        // יצירת גוף הבקשה (request body)
        JSONObject requestBody = new JSONObject().put("inputs", prompt);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(HF_API_URL, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return extractSentences(response.getBody());
        } else {
            System.err.println("Error fetching sentences for word: " + word + " | Response: " + response.getBody());
        }

        return Collections.emptyList();
    }





    private List<String> extractSentences(String jsonResponse) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String text = jsonObject.getString("generated_text");

                // פיצול טקסט למשפטים
                String[] sentences = text.split("\\. ");
                return List.of(sentences).subList(0, Math.min(2, sentences.length)); // החזרת שני המשפטים הראשונים
            }
        } catch (Exception e) {
            System.err.println("Error parsing response: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
