package com.lingotower.util;

import com.lingotower.model.Word;
import com.lingotower.dto.translation.TranslationResponseDTO;

public class TranslationUtils {

    // פונקציה עזר להיפוך תרגום
    public static TranslationResponseDTO convertWordToDTO(Word word, String sourceLang, String targetLang) {
        if ("he".equals(sourceLang) && "en".equals(targetLang)) {
            return new TranslationResponseDTO(word.getTranslation(), word.getWord()); // היפוך עברית  אנגלית
        } else {
            return new TranslationResponseDTO(word.getWord(), word.getTranslation()); //כמו שהכנסתי את הטבלאות אנגלית  עברית 
        }
    }
}
