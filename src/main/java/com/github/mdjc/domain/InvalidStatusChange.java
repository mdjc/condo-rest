package com.github.mdjc.domain;

public class InvalidStatusChange extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidStatusChange(String message) {
		super(message);
	}
}
