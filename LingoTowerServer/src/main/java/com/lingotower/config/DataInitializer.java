package com.lingotower.config;

import com.lingotower.service.DataCleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DataCleaningService dataCleaningService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting data cleaning process...");
        dataCleaningService.cleanExampleSentenceText();
        System.out.println("Data cleaning process completed.");
    }
}
