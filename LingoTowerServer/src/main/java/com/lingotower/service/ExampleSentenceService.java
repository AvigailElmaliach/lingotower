
package com.lingotower.service;

import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import com.lingotower.data .ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.exampleSentence.ExampleSentenceCreateDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExampleSentenceService {

    @Autowired
    private ExampleSentenceRepository exampleSentenceRepository;

    @Autowired
    private WordRepository wordRepository;


    public ExampleSentenceCreateDTO getTwoExampleSentencesForWord(String word) {
        Optional<Word> optionalWord = wordRepository.findByWord(word);
        if (optionalWord.isPresent()) {
            Word foundWord = optionalWord.get();
            List<ExampleSentence> sentences = exampleSentenceRepository.findByWord(foundWord);
            List<String> sentenceTexts = sentences.stream()
                    .map(ExampleSentence::getSentenceText)
                    .limit(2)
                    .collect(Collectors.toList());
            return new ExampleSentenceCreateDTO(foundWord.getWord(), sentenceTexts);
        }
        return null; // או לזרוק חריגה מתאימה
    }
}