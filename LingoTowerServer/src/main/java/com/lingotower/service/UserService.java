@Transactional
	    public void updateUserById(Long id, UserUpdateDTO userUpdateDTO) {
	        logger.info("Attempting to update user with ID: {}", id);
	        logger.debug("Received UserUpdateDTO: {}", userUpdateDTO);

	        User user = userRepository.findById(id)
	                .orElseThrow(() -> {
	                    logger.warn("User not found with ID: {}", id);
	                    return new UserNotFoundException("User not found with ID: " + id);
	                });

	        logger.debug("Retrieved user from database: {}", user);

	        // טיפול בעדכון סיסמה
	        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
	        	 user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
	        }
	        // עדכון שאר הפרטים
	        logger.debug("Updating username from '{}' to '{}'", user.getUsername(), userUpdateDTO.getUsername());
	        user.setUsername(userUpdateDTO.getUsername());
	        logger.debug("Updating email from '{}' to '{}'", user.getEmail(), userUpdateDTO.getEmail());
	        user.setEmail(userUpdateDTO.getEmail());
	        logger.debug("Updating source language from '{}' to '{}'", user.getSourceLanguage(), userUpdateDTO.getSourceLanguage());
	        user.setSourceLanguage(userUpdateDTO.getSourceLanguage());
	        logger.debug("Updating target language from '{}' to '{}'", user.getTargetLanguage(), userUpdateDTO.getTargetLanguage());
	        user.setTargetLanguage(userUpdateDTO.getTargetLanguage());

	        logger.debug("User object before saving: {}", user);
	        userRepository.save(user);
	        logger.info("User with ID {} updated successfully.", id);
	    }