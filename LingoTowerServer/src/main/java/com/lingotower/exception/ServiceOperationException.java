package com.lingotower.exception;

public class ServiceOperationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceOperationException(String message) {
		super(message);
	}

	public ServiceOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
