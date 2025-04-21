package com.lingotower.controller;

import com.lingotower.dto.exampleSentence.ExampleSentenceCreateDTO;
import com.lingotower.service.ExampleSentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sentences")
public class ExampleSentenceController {

	@Autowired
    private ExampleSentenceService exampleSentenceService;

//    @GetMapping("/word/{word}")
//    public ResponseEntity<ExampleSentenceCreateDTO> getTwoSentencesForWord(@PathVariable String word) {
//    	ExampleSentenceCreateDTO result = exampleSentenceService.getTwoExampleSentencesForWord(word);
//        if (result != null) {
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
	@GetMapping("/word/{word}")
	public ResponseEntity<ExampleSentenceCreateDTO> getTwoSentencesForWord(@PathVariable String word, Principal principal) {
	    String username = principal.getName();
	    ExampleSentenceCreateDTO result = exampleSentenceService.getTwoExampleSentencesForWord(word, username);
	    if (result != null) {
	        return new ResponseEntity<>(result, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}

}