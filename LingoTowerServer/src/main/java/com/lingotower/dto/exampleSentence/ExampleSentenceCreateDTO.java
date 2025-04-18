package com.lingotower.dto.exampleSentence;

import java.util.List;

public class ExampleSentenceCreateDTO {

    private String word;
    private List<String> sentences;
 
    public ExampleSentenceCreateDTO(String word, List<String> sentences) {
        this.word = word;
        this.sentences = sentences;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }
}
