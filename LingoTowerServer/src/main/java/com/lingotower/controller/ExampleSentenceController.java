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

	/**
	 * Retrieves two example sentences for a given word for the authenticated user.
	 * The language of the sentences will be based on the user's target language.
	 * @param word The word for which to retrieve example sentences.
	 * @param principal The Principal object representing the currently logged-in user.
	 * @return ResponseEntity containing an ExampleSentenceCreateDTO with two example sentences
	 * and HTTP status OK if sentences are found. Returns HTTP status NOT FOUND if no sentences are found for the word.
	 */
	@GetMapping("/word/{word}")
	public ResponseEntity<ExampleSentenceCreateDTO> getTwoSentencesForWord(@PathVariable String word,
			Principal principal) {
		String username = principal.getName();
		ExampleSentenceCreateDTO result = exampleSentenceService.getTwoExampleSentencesForWord(word, username);
		if (result != null) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}