package com.lingotower.config;

import com.lingotower.service.DataCleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private DataCleaningService dataCleaningService;

	/**
	 * Runs after the application context is loaded to perform initial data
	 * operations. In this case, it triggers the data cleaning process for example
	 * sentences.
	 * 
	 * @param args Command line arguments passed to the application.
	 * @throws Exception If an error occurs during the execution of this method.
	 */
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Starting data cleaning process...");
		dataCleaningService.cleanExampleSentenceText();
		System.out.println("Data cleaning process completed.");
	}
}