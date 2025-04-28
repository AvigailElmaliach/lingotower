package com.lingotower.exception;

public class CategoryAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CategoryAlreadyExistsException(String message) {
		super(message);
	}
}
