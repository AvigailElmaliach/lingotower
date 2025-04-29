package com.lingotower.service;

import com.lingotower.dto.*;
import com.lingotower.dto.translation.TranslationRequestDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TranslationService {

	private static final String TRANSLATE_API_URL = "https://api.mymemory.translated.net/get?q={text}&langpair={source}|{target}";
	private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

	/**
	 * Translates a given word from a source language to a target language using an
	 * external API. It constructs the API URL and uses RestTemplate to make the
	 * request. The translated text is extracted from the TranslationResponse
	 * object. If the response is null or an error occurs, it returns null, but also
	 * throws a RuntimeException in case of a critical error.
	 * 
	 * @param word       The word to be translated.
	 * @param sourceLang The source language code (e.g., "en").
	 * @param targetLang The target language code (e.g., "he").
	 * @return The translated text, or null if translation fails.
	 */
	public String translateText(String word, String sourceLang, String targetLang) {
		RestTemplate restTemplate = new RestTemplate();
		sourceLang = sourceLang.trim();
		targetLang = targetLang.trim();

		String url = UriComponentsBuilder.fromUriString(TRANSLATE_API_URL).buildAndExpand(word, sourceLang, targetLang)
				.toUriString();

		try {
			logger.info("Translating word: {} from {} to {}", word, sourceLang, targetLang);
			TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);

			if (response != null && response.getResponseData() != null) {
				String translatedText = response.getResponseData().getTranslatedText();
				logger.info("Translation result: {}", translatedText);
				return translatedText;
			} else {
				logger.error("Translation response is null or has no data.");
				return null;
			}
		} catch (Exception e) {
			logger.error("Error during translation: ", e);
			throw new RuntimeException("Error occurred during translation for word: " + word, e);
		}
	}

	/**
	 * Translates a given text from a source language to a target language using an
	 * external API. This method is similar to translateText but returns a default
	 * "Translation error" string in case of a failure or invalid response, and
	 * throws a RuntimeException for critical errors.
	 * 
	 * @param text       The text to be translated.
	 * @param sourceLang The source language code (e.g., "en").
	 * @param targetLang The target language code (e.g., "he").
	 * @return The translated text, or "Translation error" if translation fails.
	 */
	public String freeTranslateText(String text, String sourceLang, String targetLang) {
		RestTemplate restTemplate = new RestTemplate();
		sourceLang = sourceLang.trim();
		targetLang = targetLang.trim();

		String url = UriComponentsBuilder.fromUriString(TRANSLATE_API_URL).buildAndExpand(text, sourceLang, targetLang)
				.toUriString();

		try {
			logger.info("Translating word: {} from {} to {}", text, sourceLang, targetLang);
			TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);

			if (response != null && response.getResponseData() != null) {
				String translatedText = response.getResponseData().getTranslatedText();
				logger.info("Translation result: {}", translatedText);
				return translatedText;
			} else {
				logger.error("Translation response is null or has no data.");
				return "Translation error";
			}
		} catch (Exception e) {
			logger.error("Error during translation: ", e);
			throw new RuntimeException("Error occurred during free translation for text: " + text, e);
		}
	}

	/**
	 * Translates a given sentence from a source language to a target language, with
	 * an option to swap the source and target languages before translation. It
	 * calls the translateText method to perform the actual translation and throws a
	 * RuntimeException if an error occurs.
	 * 
	 * @param sentence      The sentence to be translated.
	 * @param sourceLang    The source language code (e.g., "en").
	 * @param targetLang    The target language code (e.g., "he").
	 * @param swapLanguages A boolean indicating whether to swap the source and
	 *                      target languages.
	 * @return The translated sentence.
	 */
	public String translateSentence(String sentence, String sourceLang, String targetLang, Boolean swapLanguages) {
		if (swapLanguages != null && swapLanguages) {
			String temp = sourceLang;
			sourceLang = targetLang;
			targetLang = temp;
		}

		try {
			return translateText(sentence, sourceLang, targetLang);
		} catch (RuntimeException e) {
			throw new RuntimeException("Error occurred during sentence translation for sentence: " + sentence, e);
		}
	}

}