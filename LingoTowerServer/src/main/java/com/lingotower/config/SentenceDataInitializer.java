package com.lingotower.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.exampleSentence.ExampleSentenceCreateDTO;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import com.lingotower.service.WordService;

@Component
@Order(3)
public class SentenceDataInitializer implements CommandLineRunner {

	@Autowired
	private WordService wordService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourcePatternResolver resourcePatternResolver;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(" אתחול משפטים לדוגמה...");
		loadExampleSentencesFromJson("classpath:sentences/*.json");
		System.out.println(" אתחול משפטים לדוגמה הסתיים.");
	}

	private void loadExampleSentencesFromJson(String locationPattern) {
		try {
			System.out.println("  טוען משפטים לדוגמה מהדפוס: " + locationPattern);
			Resource[] sentenceFiles = resourcePatternResolver.getResources(locationPattern);

			if (sentenceFiles.length == 0) {
				System.out.println("   לא נמצאו קבצי משפטים התואמים : " + locationPattern);
				return;
			}

			int sentencesLoadedCount = 0;
			for (Resource sentenceFile : sentenceFiles) {
				String filename = sentenceFile.getFilename();
				System.out.println(" קורא קובץ משפטים: " + filename);

				try {
					List<ExampleSentenceCreateDTO> wordWithSentencesList = objectMapper.readValue(
							sentenceFile.getInputStream(), new TypeReference<List<ExampleSentenceCreateDTO>>() {
							});

					for (ExampleSentenceCreateDTO wordWithSentences : wordWithSentencesList) {
						String wordStr = wordWithSentences.getWord();
						Optional<Word> existingWord = wordService.findByWord(wordStr);

						if (existingWord.isPresent()) {
							Word word = existingWord.get();
							List<String> exampleSentences = wordWithSentences.getSentences();
							if (exampleSentences != null) {
								List<ExampleSentence> newSentences = new ArrayList<>();
								if (word.getSentences() == null) {
									word.setSentences(new ArrayList<>());
								}
								for (String sentenceText : exampleSentences) {
									// בדוק אם המשפט כבר קיים (למניעת כפילויות נוספות)
									boolean exists = false;
									for (ExampleSentence existingSentence : word.getSentences()) {
										if (existingSentence.getSentenceText().equals(sentenceText)) {
											exists = true;
											break;
										}
									}
									if (!exists) {
										ExampleSentence exampleSentence = new ExampleSentence(sentenceText, word);
										newSentences.add(exampleSentence);
										sentencesLoadedCount++;
									}
								}
								// הוסף את כל המשפטים החדשים לרשימת המשפטים של המילה
								word.getSentences().addAll(newSentences);
								// שמור את המילה רק פעם אחת לאחר הוספת כל המשפטים
								wordService.saveWord(word, word.getSourceLanguage(), word.getTargetLanguage());
							}
						} else {
							System.out.println("    ⚠️ מילה '" + wordStr + "' לא נמצאה במסד הנתונים עבור משפטים.");
						}
					}
					System.out.println("    └── נקראו " + wordWithSentencesList.size() + " מילים עם משפטים מהקובץ.");

				} catch (IOException e) {
					System.err.println("    ❌ שגיאה בקריאת קובץ משפטים " + filename + ": " + e.getMessage());
				}
			}
			System.out.println("  └── סך הכל נטענו " + sentencesLoadedCount + " משפטים לדוגמה.");

		} catch (IOException e) {
			System.err.println("  ❌ שגיאה בטעינת משפטים לדוגמה: " + e.getMessage());
		}
	}
}