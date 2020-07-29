package com.gtric.exceptions;

public class OutputErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutputErrorException(String errorMessage) {
        super(errorMessage);
    }
}
