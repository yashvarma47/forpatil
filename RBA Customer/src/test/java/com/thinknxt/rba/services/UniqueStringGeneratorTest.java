package com.thinknxt.rba.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class UniqueStringGeneratorTest {

	@Test
	void generatePassword() {
		// Test
		String password = UniqueStringGenerator.generatePassword(8);

		// Assertions
		assertNotNull(password);
		assertEquals(8, password.length());
	}

	@Test
	void generatePassword_VaryingLength() {

		Set<Integer> lengths = IntStream.rangeClosed(6, 12).boxed().collect(Collectors.toSet());
		lengths.forEach(length -> {
			String password = UniqueStringGenerator.generatePassword(length);
			assertEquals(length, password.length());
		});
	}

}