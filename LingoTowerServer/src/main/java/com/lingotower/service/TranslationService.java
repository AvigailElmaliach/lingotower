package com.lingotower.service;

import com.lingotower.dto.*;//לבדוק אם צריך
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TranslationService {
    private static final String TRANSLATE_API_URL = "https://api.mymemory.translated.net/get?q={text}&langpair={source}|{target}";
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    public String translateText(String word, String sourceLang, String targetLang) {//שינתי לטקסט במקום למילה כדי שיתאים גם וגם
        RestTemplate restTemplate = new RestTemplate();
        sourceLang = sourceLang.trim();
        targetLang = targetLang.trim();

        String url = UriComponentsBuilder.fromUriString(TRANSLATE_API_URL)
                .buildAndExpand(word, sourceLang, targetLang)
                .toUriString();

        try {
            // מבצע את הקריאה ל-API
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
            return "Translation error";
        }
    }
}









/*package com.lingotower.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslationService {
    private static final String TRANSLATE_API_URL = "https://api.mymemory.translated.net/get?q={text}&langpair={source}|{target}";

    public String translateWord(String word, String sourceLang, String targetLang) {
        RestTemplate restTemplate = new RestTemplate();
        sourceLang = sourceLang.trim(); // מסיר תווים מיותרים
        targetLang = targetLang.trim(); 

        String url = TRANSLATE_API_URL.replace("{text}", word)
                                      .replace("{source}", sourceLang)
                                      .replace("{target}", targetLang);
        TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);
        return response != null ? response.getResponseData().getTranslatedText() : "Translation error";
    }
}*/
