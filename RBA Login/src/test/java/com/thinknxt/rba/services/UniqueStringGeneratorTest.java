package com.thinknxt.rba.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
 
class UniqueStringGeneratorTest {
 
    @Test
    void testGenerateUniqueNumericID() {
        // Act
        int uniqueID = UniqueStringGenerator.generateUniqueNumericID();
 
        // Assert
        assertTrue(uniqueID >= 10000000 && uniqueID < 100000000);
    }
 
    @Test
    void testGeneratePassword() {
        // Arrange
        int passwordLength = 12;
 
        // Act
        String password = UniqueStringGenerator.generatePassword(passwordLength);
 
        // Assert
        assertEquals(passwordLength, password.length());
        assertTrue(password.matches("[a-zA-Z0-9!@#$%^&*()_+]+"));
    }
}