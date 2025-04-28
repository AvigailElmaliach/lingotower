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

	public TranslationController(TranslationService translationService) {
		this.translationService = translationService;
	}

	@GetMapping("/translate")
	public ResponseEntity<String> translateText(@RequestParam String text, HttpServletRequest request) {
		String sourceLang = (String) request.getAttribute("sourceLanguage");
		String targetLang = (String) request.getAttribute("targetLanguage");

		String translatedText = translationService.translateText(text, sourceLang, targetLang);
		return ResponseEntity.ok(translatedText);
	}

	@PostMapping("/translate-sentence")
	public ResponseEntity<String> translateSentence(@RequestBody TranslationRequestDTO request,
			HttpServletRequest httpRequest) {

		String sourceLang = (String) httpRequest.getAttribute("sourceLanguage");
		String targetLang = (String) httpRequest.getAttribute("targetLanguage");

		String translatedSentence = translationService.translateSentence(request.getWord(), sourceLang, targetLang,
				request.getSwapLanguages());

		return ResponseEntity.ok(translatedSentence);
	}

	@PostMapping("/free-translate")
	public ResponseEntity<String> translateTextPost(@RequestBody FreeTranslationRequest request) {
		String translatedText = translationService.translateText(request.getText(), request.getSourceLang(),
				request.getTargetLang());
		return ResponseEntity.ok(translatedText);
	}

}
