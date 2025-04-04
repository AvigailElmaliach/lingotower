package com.lingotower.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import com.lingotower.data.WordRepository;

@Service
public class ExampleSentenceService {

    @Autowired
    private ExampleSentenceRepository ExampleSentenceRepository;

    @Autowired
    private WordRepository wordRepository;

    public void createSentence(String word) {
    	 Word wordEntity = wordRepository.findByWord(word)
                 .orElseThrow(() -> new RuntimeException("Word not found"));

        if (wordEntity == null) {
            throw new RuntimeException("Word not found");
        }

        String sentenceText = "Example sentence using the word: " + word;
        ExampleSentence sentence = new ExampleSentence(sentenceText, wordEntity);

        // שמירה במסד הנתונים
        ExampleSentenceRepository.save(sentence);
    }
}