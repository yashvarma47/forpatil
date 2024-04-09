package com.thinknxt.rba.exception;

import java.util.List;

import com.thinknxt.rba.config.Generated;

import lombok.Data;

@Data
@Generated
public class AccountDetailsNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<String> nonExistingAccountNumbers;

	public AccountDetailsNotFoundException(List<String> nonExistingAccountNumbers, String message) {
		super(message);
		this.nonExistingAccountNumbers = nonExistingAccountNumbers;
	}

	public List<String> getNonExistingAccountNumbers() {
		return nonExistingAccountNumbers;
	}

}
