package com.lingotower.controller;

import com.lingotower.model.Question;
import com.lingotower.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {

	private final QuestionService questionService;

	/**
	 * Constructor for the QuestionController, injecting the required service.
	 * 
	 * @param questionService The service layer for question-related operations.
	 */
	public QuestionController(QuestionService questionService) {
		this.questionService = questionService;
	}

	/**
	 * Retrieves a list of all questions.
	 * 
	 * @return A list of all Question objects.
	 */
	@GetMapping
	public List<Question> getAllQuestions() {
		return questionService.getAllQuestions();
	}

	/**
	 * Retrieves a specific question by its ID.
	 * 
	 * @param id The ID of the question to retrieve.
	 * @return ResponseEntity containing the Question object and HTTP status OK if
	 *         found, or HTTP status NOT FOUND if the question does not exist.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
		Optional<Question> question = questionService.getQuestionById(id);
		return question.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Retrieves all questions associated with a specific quiz ID.
	 * 
	 * @param quizId The ID of the quiz.
	 * @return A list of Question objects belonging to the specified quiz.
	 */
	@GetMapping("/quiz/{quizId}")
	public List<Question> getQuestionsByQuiz(@PathVariable Long quizId) {
		return questionService.getQuestionsByQuizId(quizId);
	}

	/**
	 * Adds a new question.
	 * 
	 * @param question The Question object to add.
	 * @return The saved Question object.
	 */
	@PostMapping
	public Question addQuestion(@RequestBody Question question) {
		return questionService.addQuestion(question);
	}

	/**
	 * Deletes a question by its ID.
	 * 
	 * @param id The ID of the question to delete.
	 * @return ResponseEntity with HTTP status NO CONTENT upon successful deletion.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
		questionService.deleteQuestion(id);
		return ResponseEntity.noContent().build();
	}
}