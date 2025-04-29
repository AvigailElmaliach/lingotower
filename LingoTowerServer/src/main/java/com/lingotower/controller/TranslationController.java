package com.lingotower.controller;

import com.lingotower.dto.translation.FreeTranslationRequest;
import com.lingotower.dto.translation.TranslationRequestDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

	private final TranslationService translationService;

	/**
	 * Constructor for the TranslationController, injecting the required service.
	 * 
	 * @param translationService The service layer for translation operations.
	 */
	public TranslationController(TranslationService translationService) {
		this.translationService = translationService;
	}

	/**
	 * Translates a given text based on the source and target languages set in the
	 * request attributes by the LanguageInterceptor.
	 * 
	 * @param text    The text to be translated, provided as a query parameter.
	 * @param request The HttpServletRequest, used to retrieve the source and target
	 *                languages.
	 * @return ResponseEntity containing the translated text and HTTP status OK.
	 */
	@GetMapping("/translate")
	public ResponseEntity<String> translateText(@RequestParam String text, HttpServletRequest request) {
		String sourceLang = (String) request.getAttribute("sourceLanguage");
		String targetLang = (String) request.getAttribute("targetLanguage");

		String translatedText = translationService.translateText(text, sourceLang, targetLang);
		return ResponseEntity.ok(translatedText);
	}

	/**
	 * Translates a word and potentially swaps the source and target languages
	 * before translation. The source and target languages are retrieved from the
	 * request attributes set by the LanguageInterceptor.
	 * 
	 * @param request     A TranslationRequestDTO containing the word to translate
	 *                    and a flag to swap languages.
	 * @param httpRequest The HttpServletRequest, used to retrieve the source and
	 *                    target languages.
	 * @return ResponseEntity containing the translated sentence (which is just the
	 *         translated word in this context) and HTTP status OK.
	 */
	@PostMapping("/translate-sentence")
	public ResponseEntity<String> translateSentence(@RequestBody TranslationRequestDTO request,
			HttpServletRequest httpRequest) {

		String sourceLang = (String) httpRequest.getAttribute("sourceLanguage");
		String targetLang = (String) httpRequest.getAttribute("targetLanguage");

		String translatedSentence = translationService.translateSentence(request.getWord(), sourceLang, targetLang,
				request.getSwapLanguages());

		return ResponseEntity.ok(translatedSentence);
	}

	/**
	 * Translates a given text with explicitly provided source and target languages
	 * in the request body.
	 * 
	 * @param request A FreeTranslationRequest containing the text to translate and
	 *                the source and target languages.
	 * @return ResponseEntity containing the translated text and HTTP status OK.
	 */
	@PostMapping("/free-translate")
	public ResponseEntity<String> translateTextPost(@RequestBody FreeTranslationRequest request) {
		String translatedText = translationService.translateText(request.getText(), request.getSourceLang(),
				request.getTargetLang());
		return ResponseEntity.ok(translatedText);
	}
}