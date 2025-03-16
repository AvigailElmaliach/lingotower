package com.lingotower.controller;

import com.lingotower.service.TranslationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping
    public String translateText(@RequestParam String text, 
                                @RequestParam String source, 
                                @RequestParam String target) {
        return translationService.translateText(text, source, target);
    }
}

