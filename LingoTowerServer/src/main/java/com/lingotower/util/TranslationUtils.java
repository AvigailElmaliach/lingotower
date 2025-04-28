package com.lingotower.util;

import com.lingotower.model.Word;
import com.lingotower.dto.translation.TranslationResponseDTO;

public class TranslationUtils {

    public static TranslationResponseDTO convertWordToDTO(Word word, String sourceLang, String targetLang) {
        if ("he".equals(sourceLang) && "en".equals(targetLang)) {
            return new TranslationResponseDTO(word.getTranslation(), word.getWord());
        } else {
            return new TranslationResponseDTO(word.getWord(), word.getTranslation());  
        }
    }
}
