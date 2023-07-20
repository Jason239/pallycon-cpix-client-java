package com.pallycon.cpix.exception;

public class CpixClientException extends Exception {
	public CpixClientException(String message) {
		super(message);
	}

	public CpixClientException(String message, Throwable cause) {
		super(message, cause);
	}
}