package com.lingotower.model;


import java.util.Set;

import com.lingotower.constants.LanguageConstants;

public class Language {
    public static final Set<String> SUPPORTED_LANGUAGES = Set.of(LanguageConstants.ENGLISH, LanguageConstants.HEBREW);
    public static boolean isValidLanguage(String lang) {
        return SUPPORTED_LANGUAGES.contains(lang.toLowerCase());
    }
}

