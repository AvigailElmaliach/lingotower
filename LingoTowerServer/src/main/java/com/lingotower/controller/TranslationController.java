package com.lingotower.controller;

import com.lingotower.dto.translation.TranslationRequestDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.service.TranslationService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Endpoint to translate a word or text from a source language to a target language.
     * Uses query parameters to pass the text and language codes.
     *
     * @param text        the word or text to be translated
     * @param source      the source language code (e.g., "en" for English)
     * @param target      the target language code (e.g., "he" for Hebrew)
     * @return the translated text as a String
     */
    @GetMapping("/translate")
    public ResponseEntity<String> translateText(@RequestParam String text, 
                                               @RequestParam String source, 
                                               @RequestParam String target) {
        String translatedText = translationService.translateText(text, source, target);
        return ResponseEntity.ok(translatedText);
    }

    /**
     * Endpoint to translate a list of words from a source language to a target language.
     * The input is wrapped in a list of TranslationRequestDTO objects.
     * Each word will be translated and the result will be returned in a list of TranslationResponseDTO objects.
     *
     * @param translationRequestList a list of TranslationRequestDTO objects containing words to be translated
     * @return a list of TranslationResponseDTO objects containing the translated words
     */
    @PostMapping("/translate-batch")
    public ResponseEntity<List<TranslationResponseDTO>> translateWords(@RequestBody List<TranslationRequestDTO> translationRequestList) {
        List<TranslationResponseDTO> translatedWords = translationService.translateWords(translationRequestList);
        return ResponseEntity.status(HttpStatus.OK).body(translatedWords);
    }
}

























//package com.lingotower.controller;
//
//import com.lingotower.service.TranslationService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/translate")
//public class TranslationController {
//
//    private final TranslationService translationService;
//
//    public TranslationController(TranslationService translationService) {
//        this.translationService = translationService;
//    }
//
//    @GetMapping
//    public String translateText(@RequestParam String text, 
//                                @RequestParam String source, 
//                                @RequestParam String target) {
//        return translationService.translateText(text, source, target);
//    }
//}

