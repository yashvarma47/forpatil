package com.thinknxt.rba.services;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for generating unique numeric IDs and passwords.
 */
@Slf4j
public class UniqueStringGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+";

    /**
     * Main method for testing the generation of a unique numeric ID.
     * @return returns void
     */
    public static void main(String[] args) {
        int uniqueID = generateUniqueNumericID();
        log.info("Unique Numeric ID: " + uniqueID);
    }

    /**
     * Generates a unique numeric ID by combining the current timestamp and a random number.
     * 
     * @return A unique numeric ID.
     */
    public static int generateUniqueNumericID() {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000000, 100000000);
        String combinedID = String.valueOf(timestamp) + String.valueOf(randomNum);
        String uniqueID = combinedID.substring(Math.max(combinedID.length() - 8, 0));
        return Integer.parseInt(uniqueID);
    }

    /**
     * Generates a random password of the specified length.
     * 
     * @param length The length of the password to generate.
     * @return A randomly generated password.
     */
    public static String generatePassword(int length) {
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
