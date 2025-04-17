package com.lingotower.dto.exampleSentence;

import java.util.List;

public class ExampleSentenceCreateDTO {

    private String word;
    private List<String> exampleSentences;
 
    public ExampleSentenceCreateDTO(String word, List<String> exampleSentences) {
        this.word = word;
        this.exampleSentences = exampleSentences;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getExampleSentences() {
        return exampleSentences;
    }

    public void setExampleSentences(List<String> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }
}
