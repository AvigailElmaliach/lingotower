package com.lingotower.controller;

import com.lingotower.dto.quiz.QuestionDTO;
import com.lingotower.model.Difficulty;
import com.lingotower.service.CompletionPracticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/completion-practice")
public class CompletionPracticeController {

	private final CompletionPracticeService completionPracticeService;

	/**
	 * Constructor for the CompletionPracticeController, injecting the required
	 * service.
	 * 
	 * @param completionPracticeService The service layer for completion practice
	 *                                  operations.
	 */
	public CompletionPracticeController(CompletionPracticeService completionPracticeService) {
		this.completionPracticeService = completionPracticeService;
	}

	/**
	 * Generates a single completion practice question for a specific category and
	 * difficulty for the authenticated user.
	 * 
	 * @param categoryName The name of the category for the question.
	 * @param difficulty   The difficulty level of the question.
	 * @param principal    The Principal object representing the currently logged-in
	 *                     user.
	 * @return ResponseEntity containing an Optional of QuestionDTO and HTTP status
	 *         OK. The Optional will contain the generated question if successful,
	 *         or be empty if no suitable word is found.
	 */
	@GetMapping("/generate")
	public ResponseEntity<Optional<QuestionDTO>> generateCompletionPractice(@RequestParam String categoryName,
			@RequestParam Difficulty difficulty, Principal principal) {
		String username = principal.getName();
		Optional<QuestionDTO> question = completionPracticeService.generateCompletionPractice(categoryName, difficulty,
				username);
		return ResponseEntity.ok(question);
	}

	/**
	 * Generates multiple completion practice questions for a specific category and
	 * difficulty for the authenticated user. Allows specifying the number of
	 * questions to generate.
	 * 
	 * @param categoryName The name of the category for the questions.
	 * @param difficulty   The difficulty level of the questions.
	 * @param count        An optional parameter specifying the number of questions
	 *                     to generate. If not provided, a default number of
	 *                     questions will be generated.
	 * @param principal    The Principal object representing the currently logged-in
	 *                     user.
	 * @return ResponseEntity containing a list of QuestionDTO and HTTP status OK.
	 *         The list will contain the generated questions.
	 */
	@GetMapping("/generate-multiple")
	public ResponseEntity<List<QuestionDTO>> generateMultipleCompletionPractices(@RequestParam String categoryName,
			@RequestParam Difficulty difficulty, @RequestParam(required = false) Integer count, Principal principal) {
		String username = principal.getName();
		List<QuestionDTO> questions = completionPracticeService.generateMultipleCompletionPractices(categoryName,
				difficulty, count, username);
		return ResponseEntity.ok(questions);
	}
}