package com.lingotower.model;


import java.util.Set;

public class Language {
    public static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "he");
    public static boolean isValidLanguage(String lang) {
        return SUPPORTED_LANGUAGES.contains(lang.toLowerCase());
    }
}

