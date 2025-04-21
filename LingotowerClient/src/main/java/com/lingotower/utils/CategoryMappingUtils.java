package com.lingotower.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.lingotower.model.Category;
import com.lingotower.service.CategoryService;

/**
 * Utility class for category mapping operations
 */
public class CategoryMappingUtils {

	private static final Logger logger = LoggingUtility.getLogger(CategoryMappingUtils.class);

	// Static mapping of Hebrew to English category names
	private static final Map<String, String> HEBREW_TO_ENGLISH_MAP = initializeHebrewToEnglishMap();

	private CategoryMappingUtils() {
		// Private constructor to prevent instantiation
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Initialize the Hebrew to English category mapping
	 */
	private static Map<String, String> initializeHebrewToEnglishMap() {
		Map<String, String> map = new HashMap<>();

		// Populate with known mappings
		map.put("חיי יום-יום ואוצר מילים בסיסי", "Everyday Life and Essential Vocabulary");
		map.put("אנשים ויחסים", "People and Relationships");
		map.put("עבודה וחינוך", "Work and Education");
		map.put("בריאות ורווחה", "Health and Well-being");
		map.put("נסיעות ופנאי", "Travel and Leisure");
		map.put("סביבה וטבע", "Environment and Nature");

		return map;
	}

	/**
	 * Maps between Hebrew and English category names.
	 * 
	 * @param categoryName    The category name to map (could be Hebrew or English)
	 * @param toEnglish       True if converting from Hebrew to English, false for
	 *                        English to Hebrew
	 * @param categoryService Optional category service to look up mappings if not
	 *                        in static map
	 * @return The mapped category name or the original if no mapping is found
	 */
	public static String mapCategoryName(String categoryName, boolean toEnglish, CategoryService categoryService) {
		if (categoryName == null || categoryName.isEmpty()) {
			return categoryName;
		}

		// First, try the direct mapping
		if (toEnglish) {
			if (HEBREW_TO_ENGLISH_MAP.containsKey(categoryName)) {
				String englishName = HEBREW_TO_ENGLISH_MAP.get(categoryName);
				logger.debug("Mapped Hebrew '{}' to English '{}'", categoryName, englishName);
				return englishName;
			}
		} else {
			// Find the Hebrew equivalent of an English name
			for (Map.Entry<String, String> entry : HEBREW_TO_ENGLISH_MAP.entrySet()) {
				if (entry.getValue().equals(categoryName)) {
					String hebrewName = entry.getKey();
					logger.debug("Mapped English '{}' to Hebrew '{}'", categoryName, hebrewName);
					return hebrewName;
				}
			}
		}

		// If direct mapping failed and we have a category service, try to find it there
		if (categoryService != null) {
			try {
				List<Category> allCategories = categoryService.getAllCategories();

				for (Category cat : allCategories) {
					if (toEnglish) {
						// Looking for Hebrew -> English
						if (cat.getTranslation() != null && cat.getTranslation().equals(categoryName)) {
							logger.debug("Found category mapping: Hebrew '{}' to English '{}'", categoryName,
									cat.getName());
							return cat.getName();
						}
					} else {
						// Looking for English -> Hebrew
						if (cat.getName().equals(categoryName) && cat.getTranslation() != null) {
							logger.debug("Found category mapping: English '{}' to Hebrew '{}'", categoryName,
									cat.getTranslation());
							return cat.getTranslation();
						}
					}
				}
			} catch (Exception e) {
				logger.error("Error while trying to map category name: {}", e.getMessage());
			}
		}

		// Return original if no mapping found
		logger.warn("No mapping found for category name: {}", categoryName);
		return categoryName;
	}

	/**
	 * Maps a category ID to its name based on common categories
	 */
	public static Long getCategoryIdByName(String categoryName) {
		switch (categoryName) {
		case "Everyday Life and Essential Vocabulary":
			return 1L;
		case "People and Relationships":
			return 2L;
		case "Work and Education":
			return 3L;
		case "Health and Well-being":
			return 4L;
		case "Travel and Leisure":
			return 5L;
		case "Environment and Nature":
			return 6L;
		default:
			// If not found, try to match partially
			if (categoryName.contains("Everyday") || categoryName.contains("Essential"))
				return 1L;
			if (categoryName.contains("People") || categoryName.contains("Relationship"))
				return 2L;
			if (categoryName.contains("Work") || categoryName.contains("Education"))
				return 3L;
			if (categoryName.contains("Health") || categoryName.contains("Well"))
				return 4L;
			if (categoryName.contains("Travel") || categoryName.contains("Leisure"))
				return 5L;
			if (categoryName.contains("Environment") || categoryName.contains("Nature"))
				return 6L;
			return 1L; // Default to first category
		}
	}
}