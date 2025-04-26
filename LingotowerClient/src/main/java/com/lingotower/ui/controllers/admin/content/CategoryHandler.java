package com.lingotower.ui.controllers.admin.content;

import java.util.List;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.WordService;
import com.lingotower.ui.controllers.admin.ContentManagementController;
import com.lingotower.utils.LoggingUtility;
import com.lingotower.utils.ui.BackgroundTask;
import com.lingotower.utils.ui.DialogUtils;

import javafx.application.Platform;
import javafx.collections.ObservableList;

/**
 * Handler class for Category-related operations in the
 * ContentManagementController
 */
public class CategoryHandler {
	private static final Logger logger = LoggingUtility.getLogger(CategoryHandler.class);

	private final ContentManagementController controller;
	private final CategoryService categoryService;
	private final WordService wordService;

	private Category selectedCategory;
	private boolean isEditMode = false;

	public CategoryHandler(ContentManagementController controller, CategoryService categoryService) {
		this.controller = controller;
		this.categoryService = categoryService;
		this.wordService = new WordService(categoryService);
	}

	/**
	 * Load all categories from the server
	 */
	public void loadCategories() {
		BackgroundTask.run("Loading categories", startTime -> {
			controller.showStatusMessage("Loading categories...", false);

			List<Category> categories = categoryService.getAllCategories();

			Platform.runLater(() -> {
				ObservableList<Category> categoryList = controller.getCategoryList();
				categoryList.clear();

				if (categories != null && !categories.isEmpty()) {
					categoryList.addAll(categories);
					controller.updateCategoryComboBoxes();
					controller.showStatusMessage("Loaded " + categories.size() + " categories successfully", false);
					LoggingUtility.logPerformance(logger, "load_categories", System.currentTimeMillis() - startTime,
							"success");
				} else {
					controller.showStatusMessage("No categories found or unable to connect to server", true);
					LoggingUtility.logPerformance(logger, "load_categories", System.currentTimeMillis() - startTime,
							"failed");
				}
			});
		});
	}

	/**
	 * Reset the category form for adding a new category
	 */
	public void resetCategoryForm() {
		controller.getCategoryFormTitle().setText("Add New Category");
		controller.getCategoryEditForm().setVisible(true);
		this.selectedCategory = null;
		this.isEditMode = false;
	}

	/**
	 * Show the edit form for a category
	 */
	public void showEditForm(Category category) {
		if (category == null) {
			logger.warn("Cannot edit null category");
			return;
		}

		logger.info("Showing edit form for category: {} (ID: {})", category.getName(), category.getId());

		controller.getCategoryFormTitle().setText("Edit Category");
		controller.getCategoryEditForm().setVisible(true);
		this.selectedCategory = category;
		this.isEditMode = true;
	}

	/**
	 * Save a new or existing category
	 */
	public void saveCategory(String name, String translation) {
		if (name.isEmpty()) {
			logger.warn("Category validation failed: name is empty");
			controller.showStatusMessage("Category name cannot be empty", true);
			return;
		}

		BackgroundTask.run("Save Category", startTime -> {
			try {
				if (isEditMode && selectedCategory != null) {
					// Update existing category
					selectedCategory.setName(name);
					selectedCategory.setTranslation(translation);
					Category updatedCategory = categoryService.updateCategory(selectedCategory.getId(),
							selectedCategory);

					Platform.runLater(() -> processCategorySaveResult(updatedCategory, name, "update", startTime));
				} else {
					// Create new category
					Category newCategory = new Category();
					newCategory.setName(name);
					newCategory.setTranslation(translation);
					Category createdCategory = categoryService.addCategory(newCategory);

					Platform.runLater(() -> processCategorySaveResult(createdCategory, name, "create", startTime));
				}
			} catch (Exception e) {
				Platform.runLater(() -> {
					logger.error("Error saving category: {}", e.getMessage(), e);
					controller.showStatusMessage("Error saving category: " + e.getMessage(), true);
					LoggingUtility.logAction(logger, isEditMode ? "update" : "create",
							controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
									: "system",
							"category:" + name, "error: " + e.getMessage());
				});
			}
		});
	}

	/**
	 * Process the result of saving a category
	 */
	private void processCategorySaveResult(Category result, String name, String action, long startTime) {
		if (result != null) {
			logger.info("Category {} successfully", action.equals("update") ? "updated" : "created");
			controller.showStatusMessage("Category " + action + "d successfully", false);

			LoggingUtility.logAction(logger, action,
					controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername() : "system",
					"category:" + name, "success");

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, action + "_category", duration, "success");
		} else {
			logger.warn("Failed to {} category: service returned null", action);
			controller.showStatusMessage("Failed to " + action + " category", true);

			long duration = System.currentTimeMillis() - startTime;
			LoggingUtility.logPerformance(logger, action + "_category", duration, "failed");
		}

		// Refresh and hide form
		loadCategories();
		controller.getCategoryEditForm().setVisible(false);
		if (controller.getParentView() != null) {
			controller.getParentView().refresh();
		}
	}

	/**
	 * Delete a category and its associated words
	 */
	public void deleteCategory(Category category) {
		if (category == null) {
			logger.warn("Cannot delete null category");
			return;
		}

		logger.info("Delete requested for category: {} (ID: {})", category.getName(), category.getId());

		if (DialogUtils.showDeleteConfirmation("category", category.getName())) {
			BackgroundTask.run("Delete Category", startTime -> {
				try {
					// Delete associated words first
					List<Word> words = wordService.getWordsByCategory(category.getId());
					logger.info("Deleting {} words associated with category", words.size());

					for (Word word : words) {
						wordService.deleteWord(word.getId());
					}

					// Delete the category
					logger.info("Deleting category with ID: {}", category.getId());
					boolean success = categoryService.deleteCategory(category.getId());

					Platform.runLater(() -> {
						if (success) {
							logger.info("Category deleted successfully");
							controller.showStatusMessage("Category deleted successfully", false);
							loadCategories();
							if (controller.getParentView() != null) {
								controller.getParentView().refresh();
							}

							LoggingUtility.logAction(logger, "delete",
									controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
											: "system",
									"category:" + category.getName(), "success");
						} else {
							logger.warn("Failed to delete category: service returned false");
							controller.showStatusMessage("Failed to delete category", true);
						}
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						logger.error("Error deleting category: {}", e.getMessage(), e);
						controller.showStatusMessage("Error deleting category: " + e.getMessage(), true);
						LoggingUtility.logAction(logger, "delete",
								controller.getCurrentAdmin() != null ? controller.getCurrentAdmin().getUsername()
										: "system",
								"category:" + category.getName(), "error: " + e.getMessage());
					});
				}
			});
		} else {
			logger.info("Category deletion cancelled by user");
		}
	}
}