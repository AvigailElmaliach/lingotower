package com.lingotower.service;

import com.lingotower.data.WordRepository;
import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Question;
import com.lingotower.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CompletionPracticeService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ExampleSentenceRepository exampleSentenceRepository;

    @Autowired
    private CategoryService categoryService;

    private final Random random = new Random();

    public Optional<Question> generateCompletionPractice(String categoryName, Difficulty difficulty) {
        // 1. שליפת מילים לפי קטגוריה ורמה
        Optional<Category> categoryOptional = categoryService.findByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return Optional.empty();
        }
        Category category = categoryOptional.get();
        List<Word> words = wordRepository.findByCategoryAndDifficulty(category, difficulty);

        if (words.isEmpty()) {
            return Optional.empty();
        }

        // בחירת מילה אקראית מתוך הרשימה
        Word word = words.get(random.nextInt(words.size()));

        // 2. שליפת המשפטים עבור המילה
        List<ExampleSentence> sentences = exampleSentenceRepository.findByWord(word);
        if (sentences.size() < 1) {
            return Optional.empty();
        }
        ExampleSentence selectedSentence = sentences.get(random.nextInt(sentences.size()));
        String sentenceText = selectedSentence.getSentenceText();

        // 3. בחירת מילה להחסרה
        String[] wordsInSentence = sentenceText.split("\\s+");
        int wordToReplaceIndex = -1;
        List<String> commonShortWords = List.of("a", "the", "is", "are", "in", "on", "at", "to", "for", "with");
        for (int i = 0; i < wordsInSentence.length; i++) {
            if (wordsInSentence[i].length() > 2 && !commonShortWords.contains(wordsInSentence[i].toLowerCase())) {
                wordToReplaceIndex = i;
                break;
            }
        }

        if (wordToReplaceIndex == -1) {
            return Optional.empty();
        }
        String correctAnswer = wordsInSentence[wordToReplaceIndex];
        String questionText = sentenceText.replaceFirst("\\b" + correctAnswer + "\\b", "_____");

        // 4. בחירת אופציות לא נכונות
        // כאן נצטרך לשלוף מילים אקראיות אחרות מאותה קטגוריה ורמה
        List<Word> allWordsInCategoryAndLevel = wordRepository.findByCategoryAndDifficulty(category, difficulty);
        List<String> wrongAnswers = allWordsInCategoryAndLevel.stream()
                .filter(w -> !w.getWord().equalsIgnoreCase(correctAnswer))
                .map(Word::getWord)
                .distinct()
                .limit(4)
                .collect(Collectors.toList());

        // 5. יצירת אובייקט Question עבור התרגול
        Question question = new Question();
        question.setQuestionText(questionText);
        question.setCorrectAnswer(correctAnswer);
        question.setWrongAnswers(wrongAnswers);
        question.setCategory(category);
        question.setDifficulty(difficulty);

        return Optional.of(question);
    }
}