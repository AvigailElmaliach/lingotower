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

	public CompletionPracticeController(CompletionPracticeService completionPracticeService) {
		this.completionPracticeService = completionPracticeService;
	}

	@GetMapping("/generate")
	public ResponseEntity<Optional<QuestionDTO>> generateCompletionPractice(@RequestParam String categoryName,
			@RequestParam Difficulty difficulty, Principal principal) {
		String username = principal.getName();
		Optional<QuestionDTO> question = completionPracticeService.generateCompletionPractice(categoryName, difficulty,
				username);
		return ResponseEntity.ok(question);
	}

	@GetMapping("/generate-multiple")
	public ResponseEntity<List<QuestionDTO>> generateMultipleCompletionPractices(@RequestParam String categoryName,
			@RequestParam Difficulty difficulty, @RequestParam(required = false) Integer count, Principal principal) {
		String username = principal.getName();
		List<QuestionDTO> questions = completionPracticeService.generateMultipleCompletionPractices(categoryName,
				difficulty, count, username);
		return ResponseEntity.ok(questions);
	}
}