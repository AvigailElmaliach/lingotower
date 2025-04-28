package com.lingotower.exception;

public class WordNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WordNotFoundException(String message) {
		super(message);
	}
}
