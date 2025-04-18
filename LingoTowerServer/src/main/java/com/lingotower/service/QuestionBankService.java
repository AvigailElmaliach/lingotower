package com.lingotower.service;

import com.lingotower.data.WordRepository;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class QuestionBankService {

    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private WordService wordService;
    @Autowired
    private CategoryService categoryService;

    private final Random random = new Random();

    public List<WordByCategory> getRandomTranslatedWords(String categoryName, Difficulty difficulty, String userLanguage, int count) {
        Optional<Category> categoryOptional = categoryService.findByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return Collections.emptyList();
        }
        Category category = categoryOptional.get();
        List<Word> allWords = wordRepository.findByCategoryAndDifficulty(category, difficulty);
        if (allWords.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(allWords, random);
        return wordService.mapWordsToLanguage(allWords.stream().limit(count).collect(Collectors.toList()), userLanguage);
    }

    public List<String> getWrongOptions(Word correctWord, String categoryName, Difficulty difficulty, int count, String userLanguage) {
        Optional<Category> categoryOptional = categoryService.findByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return Collections.emptyList();
        }
        Category category = categoryOptional.get();
        return wordRepository.findByCategoryAndDifficulty(category, difficulty)
                .stream()
                .filter(w -> !w.getId().equals(correctWord.getId()))
                .map(w -> {
                    List<WordByCategory> translatedWords = wordService.mapWordsToLanguage(Collections.singletonList(w), userLanguage);
                    return translatedWords.isEmpty() ? w.getWord() : translatedWords.get(0).getTranslatedText();
                })
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<String> getWrongCompletionOptions(String categoryName, Difficulty difficulty, String correctAnswer, int count) {
        Optional<Category> categoryOptional = categoryService.findByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return Collections.emptyList();
        }
        Category category = categoryOptional.get();
        return wordRepository.findByCategoryAndDifficulty(category, difficulty)
                .stream()
                .filter(w -> !w.getWord().equalsIgnoreCase(correctAnswer))
                .map(Word::getWord)
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Question createQuizQuestion(WordByCategory correctWord, List<String> wrongOptions) {
        Question question = new Question();
        question.setQuestionText(correctWord.getWord());
        question.setCorrectAnswer(correctWord.getTranslatedText());
        question.setWrongAnswers(wrongOptions.stream().limit(4).collect(Collectors.toList()));
        question.setCategory(correctWord.getCategory());
        question.setDifficulty(correctWord.getDifficulty());
        return question;
    }

    public Question createCompletionQuestion(String questionText, String correctAnswer, List<String> wrongAnswers, Category category, Difficulty difficulty) {
        Question question = new Question();
        question.setQuestionText(questionText);
        question.setCorrectAnswer(correctAnswer);
        question.setWrongAnswers(wrongAnswers);
        question.setCategory(category);
        question.setDifficulty(difficulty);
        return question;
    }
}