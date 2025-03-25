package com.lingotower.service;
import com.lingotower.data.WordRepository; 
import com.lingotower.model.Word;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;



@Service
public class WordService {
	private final WordRepository wordRepository;
    private final TranslationService translationService;

	 	public WordService(WordRepository wordRepository, TranslationService translationService) {
		this.wordRepository = wordRepository;
        this.translationService = translationService;

	}

	public List<Word> getAllWords() {
		return wordRepository.findAll();
	}

	public Optional<Word> getWordById(Long id) {
		return wordRepository.findById(id);
	}

	public Word saveWord(Word word) {
		return wordRepository.save(word);
	}

	public void deleteWord(Long id) {
		wordRepository.deleteById(id);
	}
	 public Word addWordWithTranslation(String word, String sourceLang) {
		 //word = word.trim().replaceAll("[\\n\\r]", "");
		 System.out.println("---------the word is:-------"+word);
	        String targetLang = sourceLang.equals("en") ? "he" : "en";
	        String translation = translationService.translateText(word, sourceLang, targetLang);

	        Word newWord = new Word(word, translation, sourceLang);
	        return wordRepository.save(newWord);
	    }
	 public void deleteAllWords() {
		    wordRepository.deleteAll(); // מוחק את כל המילים בבסיס הנתונים
		}

}














