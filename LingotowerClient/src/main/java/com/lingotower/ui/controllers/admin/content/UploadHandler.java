package com.lingotower.ui.controllers.admin.content;

import java.io.File;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.service.WordService;
import com.lingotower.ui.controllers.admin.ContentManagementController;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ui.BackgroundTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Handler class for JSON Upload operations in the ContentManagementController
 */
public class UploadHandler {
	private static final Logger logger = LoggingUtility.getLogger(UploadHandler.class);

	private final ContentManagementController controller;
	private final WordService wordService;

	private File selectedJsonFile;

	public UploadHandler(ContentManagementController controller, WordService wordService) {
		this.controller = controller;
		this.wordService = wordService;
	}

	/**
	 * Show the upload form
	 */
	public void showUploadForm() {
		logger.info("Upload JSON button clicked");

		// Hide other forms, show upload form
		controller.getJsonUploadForm().setVisible(true);
		controller.getJsonUploadForm().setManaged(true);
		controller.getWordEditForm().setVisible(false);
		controller.getWordEditForm().setManaged(false);
		controller.getCategoryEditForm().setVisible(false);

		// Set up category dropdown
		controller.getUploadCategoryComboBox()
				.setItems(FXCollections.observableArrayList(controller.getCategoryList()));

		if (!controller.getCategoryList().isEmpty()) {
			controller.getUploadCategoryComboBox().setValue(controller.getCategoryList().get(0));
		}

		// Reset file selection
		selectedJsonFile = null;
		controller.getSelectedFileTextField().setText("No file selected");
		controller.getUploadJsonButton().setDisable(true);
	}

	/**
	 * Open a file chooser to select a JSON file
	 */
	public void selectJsonFile(Window parentWindow) {
		logger.info("Select JSON file button clicked");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Words JSON File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON Files", "*.json"));

		File file = fileChooser.showOpenDialog(parentWindow);
		if (file != null) {
			selectedJsonFile = file;
			controller.getSelectedFileTextField().setText(file.getName());
			controller.getUploadJsonButton().setDisable(false);
			logger.info("JSON file selected: {}", file.getAbsolutePath());
		} else {
			logger.info("No file selected (file chooser cancelled)");
		}
	}

	/**
	 * Upload the selected JSON file
	 */
	public void uploadJson() {
		if (selectedJsonFile == null || !selectedJsonFile.exists()) {
			logger.warn("Upload failed: No file selected or file does not exist");
			controller.showStatusMessage("No file selected or file does not exist", true);
			return;
		}

		Category selectedCategory = controller.getUploadCategoryComboBox().getValue();
		if (selectedCategory == null) {
			logger.warn("Upload failed: No category selected");
			controller.showStatusMessage("Please select a category", true);
			return;
		}

		logger.info("Uploading JSON file: {} to category ID: {}", selectedJsonFile.getName(), selectedCategory.getId());

		controller.showStatusMessage("Uploading words...", false);

		BackgroundTask.run("Uploading JSON", startTime -> {
			try {
				// Use the updated WordService method to upload the JSON file
				String result = wordService.uploadWordsJson(selectedJsonFile, selectedCategory.getId());

				Platform.runLater(() -> {
					if (result != null) {
						logger.info("Words uploaded successfully: {}", result);
						controller.showStatusMessage("Words uploaded successfully: " + result, false);
						controller.getJsonUploadForm().setVisible(false);

						// Refresh the words list
						controller.getWordsList().clear();
						controller.getWordsList().addAll(wordService.getAllWords());

						LoggingUtility.logAction(logger, "upload",
								controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
										: "system",
								"json_file:" + selectedJsonFile.getName(), "success: " + result);
					} else {
						logger.warn("Upload failed: Service returned null result");
						controller.showStatusMessage("Upload failed. Please check the server logs for details.", true);
					}
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					logger.error("Error uploading words: {}", e.getMessage(), e);
					controller.showStatusMessage("Error uploading words: " + e.getMessage(), true);
					LoggingUtility
							.logAction(logger, "upload",
									controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
											: "system",
									"json_file:" + selectedJsonFile.getName(), "error: " + e.getMessage());
				});
			}
		});
	}
}