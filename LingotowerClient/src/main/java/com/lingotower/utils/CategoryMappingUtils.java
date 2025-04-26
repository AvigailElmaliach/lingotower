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
		map.put("טיולים ופנאי", "Travel and Leisure"); 
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
		if (categoryName == null) {
			logger.warn("getCategoryIdByName received a null categoryName. Returning default category ID (1L).");
			return 1L; // Return a default category ID if categoryName is null
		}

		// Trim the category name to handle leading/trailing whitespace
		String trimmedCategoryName = categoryName.trim();
		if (trimmedCategoryName.isEmpty()) {
			logger.warn(
					"getCategoryIdByName received an empty categoryName after trimming. Returning default category ID (1L).");
			return 1L;
		}

		//  Check against hardcoded English names first
		switch (trimmedCategoryName) {
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
		}

		// If not a direct English match, check if it's a known Hebrew name using the
		// map
		if (HEBREW_TO_ENGLISH_MAP.containsKey(trimmedCategoryName)) {
			String englishName = HEBREW_TO_ENGLISH_MAP.get(trimmedCategoryName);
			// Recursively call with the English name to get the ID
			// This avoids duplicating the switch logic
			return getCategoryIdByName(englishName);
		}

		// If still not found, try partial matching (original default logic)
		if (trimmedCategoryName.contains("Everyday") || trimmedCategoryName.contains("Essential"))
			return 1L;
		if (trimmedCategoryName.contains("People") || trimmedCategoryName.contains("Relationship"))
			return 2L;
		if (trimmedCategoryName.contains("Work") || trimmedCategoryName.contains("Education"))
			return 3L;
		if (trimmedCategoryName.contains("Health") || trimmedCategoryName.contains("Well"))
			return 4L;
		if (trimmedCategoryName.contains("Travel") || trimmedCategoryName.contains("Leisure"))
			return 5L;
		if (trimmedCategoryName.contains("Environment") || trimmedCategoryName.contains("Nature"))
			return 6L;

		// If no match found
		logger.warn("No specific category ID found for category name: {}. Returning default category ID (1L).",
				trimmedCategoryName);
		return 1L; // Default to first category
	}
}