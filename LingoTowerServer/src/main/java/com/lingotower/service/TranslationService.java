package com.lingotower.service;

import com.lingotower.dto.*;//לבדוק אם צריך
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

    public String translateText(String word, String sourceLang, String targetLang) {
        RestTemplate restTemplate = new RestTemplate();
        sourceLang = sourceLang.trim();
        targetLang = targetLang.trim();

        String url = UriComponentsBuilder.fromUriString(TRANSLATE_API_URL)
                .buildAndExpand(word, sourceLang, targetLang)
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
                return "Translation error";
            }
        } catch (Exception e) {
            logger.error("Error during translation: ", e);
            return "Translation errorרררר";
        }
    }

//    public List<TranslationResponseDTO> translateWords(List<TranslationRequestDTO> translationRequestList) {
//        List<TranslationResponseDTO> translatedWords = new ArrayList<>();
//        for (TranslationRequestDTO requestDTO : translationRequestList) {
//            String translatedText = translateText(requestDTO.getWord(), requestDTO.getSourceLang(), requestDTO.getTargetLang());
//            TranslationResponseDTO responseDTO = new TranslationResponseDTO();
//            responseDTO.setWord(requestDTO.getWord());
//            responseDTO.setTranslatedText(translatedText);
//            translatedWords.add(responseDTO);
//        }
//        return translatedWords;
//    }


public String translateSentence(String sentence, String sourceLang, String targetLang, Boolean swapLanguages) {
    if (swapLanguages != null && swapLanguages) {
        String temp = sourceLang;
        sourceLang = targetLang;
        targetLang = temp;
    }

    return translateText(sentence, sourceLang, targetLang);
}


}



