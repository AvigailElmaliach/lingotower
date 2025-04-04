package com.lingotower.ui.components;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.lingotower.security.TokenStorage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class TranslatorDialog {

	@FXML
	private ComboBox<String> sourceLanguageComboBox;

	@FXML
	private ComboBox<String> targetLanguageComboBox;

	@FXML
	private TextArea sourceTextArea;

	@FXML
	private TextArea targetTextArea;

	@FXML
	private Button translateButton;

	@FXML
	private Button swapButton;

	@FXML
	private Button closeButton;

	@FXML
	private Label statusLabel;

	private Stage dialogStage;
	private VBox root;
	private RestTemplate restTemplate;
	private static final String TRANSLATE_API_URL = "http://localhost:8080/api/translate/free-translate";

	/**
	 * Creates and initializes the translator dialog
	 * 
	 * @param owner The owner window for this dialog
	 */
	public TranslatorDialog(Window owner) {
		try {
			// Load FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TranslatorDialog.fxml"));
			loader.setController(this);
			root = loader.load();

			// Initialize RestTemplate
			restTemplate = new RestTemplate();

			// Create dialog stage
			dialogStage = new Stage();
			dialogStage.setTitle("Text Translator");
			dialogStage.initOwner(owner);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initStyle(StageStyle.DECORATED);
			dialogStage.setScene(new Scene(root));

			// Initialize UI
			initializeUI();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the UI components
	 */
	private void initializeUI() {
		// Set up language options
		sourceLanguageComboBox.setItems(FXCollections.observableArrayList("English", "Hebrew"));
		targetLanguageComboBox.setItems(FXCollections.observableArrayList("Hebrew", "English"));

		// Set default values
		sourceLanguageComboBox.setValue("en");
		targetLanguageComboBox.setValue("he");

		// Set up buttons
		translateButton.setOnAction(event -> handleTranslate());
		swapButton.setOnAction(event -> handleSwap());
		closeButton.setOnAction(event -> dialogStage.close());

		// Initial status
		statusLabel.setText("Enter text and click Translate");
	}

	/**
	 * Shows the dialog
	 */
	public void show() {
		dialogStage.show();
	}

	/**
	 * Handles the translate button click
	 */
	private void handleTranslate() {
		String sourceText = sourceTextArea.getText().trim();
		if (sourceText.isEmpty()) {
			statusLabel.setText("Please enter text to translate");
			return;
		}

		String sourceLang = sourceLanguageComboBox.getValue();
		String targetLang = targetLanguageComboBox.getValue();

		translateText(sourceText, sourceLang, targetLang);
	}

	/**
	 * Handles the swap button click
	 */
	private void handleSwap() {
		// Swap languages
		String tempLang = sourceLanguageComboBox.getValue();
		sourceLanguageComboBox.setValue(targetLanguageComboBox.getValue());
		targetLanguageComboBox.setValue(tempLang);

		// Swap text
		String tempText = sourceTextArea.getText();
		sourceTextArea.setText(targetTextArea.getText());
		targetTextArea.setText(tempText);
	}

	/**
	 * Translates the text using the API
	 * 
	 * @param text       The text to translate
	 * @param sourceLang The source language code
	 * @param targetLang The target language code
	 */
	private void translateText(String text, String sourceLang, String targetLang) {
		try {
			// Show loading status
			statusLabel.setText("Translating...");

			// Create request body
			TranslationRequest request = new TranslationRequest(text, sourceLang, targetLang);

			// Set up headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Add authentication token if available
			if (TokenStorage.hasToken()) {
				headers.set("Authorization", "Bearer " + TokenStorage.getToken());
			}

			// Create HTTP entity with headers and body
			HttpEntity<TranslationRequest> entity = new HttpEntity<>(request, headers);

			// Make the API call
			ResponseEntity<String> response = restTemplate.postForEntity(TRANSLATE_API_URL, entity, String.class);

			// Process response
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				String translatedText = response.getBody();
				targetTextArea.setText(translatedText);
				statusLabel.setText("Translation complete");

				// Reset status message after 3 seconds
				Timeline timeline = new Timeline(
						new KeyFrame(Duration.seconds(3), e -> statusLabel.setText("Enter text and click Translate")));
				timeline.play();
			} else {
				targetTextArea.setText("");
				statusLabel.setText("Translation failed: " + response.getStatusCode());
			}
		} catch (Exception e) {
			targetTextArea.setText("");
			statusLabel.setText("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Inner class to represent the translation request
	private static class TranslationRequest {
		private String text;
		private String sourceLang;
		private String targetLang;

		public TranslationRequest(String text, String sourceLang, String targetLang) {
			this.text = text;
			this.sourceLang = sourceLang;
			this.targetLang = targetLang;
		}

		public String getText() {
			return text;
		}

		public String getSourceLang() {
			return sourceLang;
		}

		public String getTargetLang() {
			return targetLang;
		}
	}
}