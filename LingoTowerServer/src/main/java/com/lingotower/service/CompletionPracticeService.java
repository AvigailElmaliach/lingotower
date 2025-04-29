package com.lingotower.service;

import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.quiz.QuestionDTO;
import com.lingotower.dto.mapper.QuestionMapper;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.exception.ServiceOperationException;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Question;
import com.lingotower.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompletionPracticeService {

	@Autowired
	private ExampleSentenceRepository exampleSentenceRepository;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private WordService wordService;
	@Autowired
	private QuestionBankService questionBankService;
	@Autowired
	private WordRepository wordRepository;
	@Autowired
	private QuestionMapper questionMapper;

	private final Random random = new Random();
	private static final int DEFAULT_COMPLETION_QUESTIONS = 10;

	/**
	 * Generates a fill-in-the-blank practice question.
	 * @param categoryName The name of the word category.
	 * @param difficulty The difficulty level of the question.
	 * @param username The username of the user.
	 * @return An Optional containing the generated QuestionDTO, or an empty Optional if generation fails.
	 * @throws ServiceOperationException if a critical resource is not found.
	 */
	public Optional<QuestionDTO> generateCompletionPractice(String categoryName, Difficulty difficulty,
			String username) {
		Optional<Category> categoryOptional = categoryService.findByName(categoryName);
		if (categoryOptional.isEmpty()) {
			throw new ServiceOperationException("No matching category found: " + categoryName);
		}
		Category category = categoryOptional.get();
		String userLanguage = wordService.getUserLanguage(username);
		boolean isHebrew = "he".equalsIgnoreCase(userLanguage);

		List<WordByCategory> randomWords = questionBankService.getRandomTranslatedWords(category.getName(), difficulty,
				userLanguage, 1);
		if (randomWords.isEmpty()) {
			return Optional.empty(); // It might be okay to return empty here if no words are available
		}
		WordByCategory wordByCategory = randomWords.get(0);

		Optional<Word> wordOptional = wordRepository.findById(wordByCategory.getId());
		if (wordOptional.isEmpty()) {
			throw new ServiceOperationException("No matching word found with ID: " + wordByCategory.getId());
		}
		Word word = wordOptional.get();

		List<ExampleSentence> sentences = exampleSentenceRepository.findByWord(word);
		if (sentences.isEmpty()) {
			throw new ServiceOperationException("No example sentences found for the word: " + word.getWord());
		}
		ExampleSentence selectedSentence = sentences.get(random.nextInt(sentences.size()));
		String sentenceText = selectedSentence.getSentenceText();
		String translatedSentenceText = selectedSentence.getTranslatedText();

		String correctAnswerOriginal;
		if (isHebrew && translatedSentenceText != null) {
			correctAnswerOriginal = extractCorrectAnswerHebrew(translatedSentenceText);
		} else {
			correctAnswerOriginal = extractCorrectAnswer(sentenceText);
		}
		if (correctAnswerOriginal == null) {
			return Optional.empty(); 
		}

		String questionText = isHebrew && translatedSentenceText != null
				? translatedSentenceText.replaceFirst("\\b" + Pattern.quote(correctAnswerOriginal) + "\\b", "_____")
				: sentenceText.replaceFirst("\\b" + Pattern.quote(correctAnswerOriginal) + "\\b", "_____");

		questionText = questionText.replaceAll("\\s+", " ").trim();

		String finalCorrectAnswer = correctAnswerOriginal;

		List<String> wrongAnswers = questionBankService.getWrongCompletionOptions(category.getName(), difficulty,
				correctAnswerOriginal, 4);
		if (isHebrew) {
			wrongAnswers = wrongAnswers.stream().map(wrongAnswer -> {
				Optional<Word> wrongWordOptional = wordRepository.findByWordAndCategory(wrongAnswer, category);
				return wrongWordOptional.map(Word::getTranslation).orElse(wrongAnswer);
			}).filter(Objects::nonNull).collect(Collectors.toList());
		}

		Question question = questionBankService.createCompletionQuestion(questionText, finalCorrectAnswer, wrongAnswers,
				category, difficulty);

		List<String> finalOptions = new ArrayList<>();
		finalOptions.add(question.getCorrectAnswer());
		wrongAnswers.stream().limit(4).forEach(finalOptions::add);
		Collections.shuffle(finalOptions);

		QuestionDTO dto = new QuestionDTO(question.getId(), question.getQuestionText(), finalOptions,
				question.getCorrectAnswer(), question.getCategory());
		return Optional.of(dto);
	}

	/**
	 * Extracts the correct answer from a given English sentence for completion practice.
	 * It prioritizes longer, less common words.
	 * @param sentenceText The English sentence.
	 * @return The extracted correct answer, or null if none is found.
	 */
	private String extractCorrectAnswer(String sentenceText) {
		String[] wordsInSentence = sentenceText.split("\\s+");
		List<String> commonShortWords = List.of("a", "the", "is", "are", "in", "on", "at", "to", "for", "with");

		// Starting from the second word in the sentence
		for (int i = 1; i < wordsInSentence.length; i++) {
			String word = wordsInSentence[i];
			if (word.length() > 2 && !commonShortWords.contains(word.toLowerCase())) {
				return word;
			}
		}
		return null;
	}

	/**
	 * Extracts the correct answer from a given Hebrew sentence for completion practice.
	 * It prioritizes longer, less common words (excluding common short Hebrew words).
	 * @param sentenceText The Hebrew sentence.
	 * @return The extracted correct answer, or null if none is found.
	 */
	private String extractCorrectAnswerHebrew(String sentenceText) {
		List<String> commonShortWords = List.of("של", "עם", "זה", "על", "גם", "את", "אם", "לא", "כן", "יש", "אין", "מה",
				"אני", "הוא", "היא", "אתה", "את", "אנחנו", "הם", "הן", "ב", "ל", "ו", "כ", "ש", "מ");

		String[] wordsInSentence = sentenceText.split("\\s+");

		for (int i = 1; i < wordsInSentence.length; i++) {
			String word = wordsInSentence[i].replaceAll("[^א-ת]", "").trim();
			if (word.length() > 2 && !commonShortWords.contains(word)) {
				return word;
			}
		}
		return null;
	}

	/**
	 * Generates multiple fill-in-the-blank practice questions.
	 * @param categoryName The name of the word category.
	 * @param difficulty The difficulty level of the questions.
	 * @param count The desired number of questions.
	 * @param username The username of the user.
	 * @return A list of generated QuestionDTOs.
	 */
	public List<QuestionDTO> generateMultipleCompletionPractices(String categoryName, Difficulty difficulty,
			Integer count, String username) {
		int numQuestions = (count != null) ? count : DEFAULT_COMPLETION_QUESTIONS;
		List<QuestionDTO> questions = new ArrayList<>();
		Set<String> generatedQuestions = new HashSet<>();

		for (int i = 0; i < numQuestions; i++) {
			Optional<QuestionDTO> questionOptional = generateCompletionPractice(categoryName, difficulty, username);
			questionOptional.ifPresent(question -> {
				if (!generatedQuestions.contains(question.getQuestionText())) {
					questions.add(question);
					generatedQuestions.add(question.getQuestionText());
				}
			});
			if (questions.size() == numQuestions) {
				break;
			}
		}
		return questions;
	}
}