package com.lingotower.controller;
import com.lingotower.model.Word;
import com.lingotower.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/words")  
public class WordController {
	private final WordService wordService;
	public WordController(WordService wordService) {
        this.wordService = wordService;
	}
	
    @GetMapping
    public List<Word> getAllWords() {
        return wordService.getAllWords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Word> getWordById(@PathVariable Long id) {
        Optional<Word> word = wordService.getWordById(id);
        return word.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Word> saveWord(@RequestBody Word word) {
        Word savedWord = wordService.saveWord(word);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWord);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("/add")
    public ResponseEntity<Word> addWordWithTranslation(@RequestParam String word, @RequestParam String sourceLang) {
        Word addedWord = wordService.addWordWithTranslation(word, sourceLang);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedWord);
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllWords() {
        wordService.deleteAllWords(); 
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    
}





    

        

   

