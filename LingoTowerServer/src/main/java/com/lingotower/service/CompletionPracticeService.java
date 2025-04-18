package com.lingotower.service;

import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.quiz.QuestionDTO;
import com.lingotower.dto.mapper.QuestionMapper;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Question;
import com.lingotower.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
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

    public Optional<QuestionDTO> generateCompletionPractice(String categoryName, Difficulty difficulty, String username) {
        Optional<Category> categoryOptional = categoryService.findByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return Optional.empty();
        }
        Category category = categoryOptional.get();
        String userLanguage = wordService.getUserLanguage(username);

        List<WordByCategory> randomWords = questionBankService.getRandomTranslatedWords(
                category.getName(), difficulty, userLanguage, 1);
        if (randomWords.isEmpty()) {
            return Optional.empty();
        }
        WordByCategory wordByCategory = randomWords.get(0);

        Optional<Word> wordOptional = wordRepository.findById(wordByCategory.getId());
        if (wordOptional.isEmpty()) {
            return Optional.empty();
        }
        Word word = wordOptional.get();

        List<ExampleSentence> sentences = exampleSentenceRepository.findByWord(word);
        if (sentences.isEmpty()) {
            return Optional.empty();
        }
        ExampleSentence selectedSentence = sentences.get(random.nextInt(sentences.size()));
        String sentenceText = selectedSentence.getSentenceText();

        String correctAnswer = extractCorrectAnswer(sentenceText);
        if (correctAnswer == null) {
            return Optional.empty();
        }
        String questionText = sentenceText.replaceFirst("\\b" + correctAnswer + "\\b", "_____");

        List<String> wrongAnswers = questionBankService.getWrongCompletionOptions(
                category.getName(), difficulty, correctAnswer, 4);

        Question question = questionBankService.createCompletionQuestion(
                questionText, correctAnswer, wrongAnswers, category, difficulty
        );

        // יצירת רשימת אפשרויות
        List<String> options = new ArrayList<>();
        options.addAll(question.getWrongAnswers());
        options.add(question.getCorrectAnswer());
        Collections.shuffle(options);

        // בחירת עד 4 תשובות שגויות + התשובה הנכונה
        List<String> finalOptions = new ArrayList<>();
        finalOptions.add(question.getCorrectAnswer()); // הוספת התשובה הנכונה ראשונה
        options.stream()
                .filter(option -> !option.equals(question.getCorrectAnswer())) // סינון התשובה הנכונה
                .limit(4) // לקיחת עד 4 תשובות שגויות
                .forEach(finalOptions::add);
        Collections.shuffle(finalOptions); // ערבוב הרשימה הסופית

        // יצירת ה-DTO עם רשימת האפשרויות הנכונה
        QuestionDTO dto = new QuestionDTO(
                question.getId(),
                question.getQuestionText(),
                finalOptions,
                question.getCorrectAnswer(),
                question.getCategory()
        );
        return Optional.of(dto);
    }

    private String extractCorrectAnswer(String sentenceText) {
        String[] wordsInSentence = sentenceText.split("\\s+");
        List<String> commonShortWords = List.of("a", "the", "is", "are", "in", "on", "at", "to", "for", "with");
        for (String word : wordsInSentence) {
            if (word.length() > 2 && !commonShortWords.contains(word.toLowerCase())) {
                return word;
            }
        }
        return null;
    }

    public List<QuestionDTO> generateMultipleCompletionPractices(String categoryName, Difficulty difficulty, Integer count, String username) {
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