package com.thinknxt.rba.services;
import java.util.UUID;

public class UniqueStringGenerator {

    public static String generateUniqueRandomNumber() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString().replaceAll("-", "");

        randomUUIDString = randomUUIDString.substring(0, Math.min(randomUUIDString.length(), 16));

        return randomUUIDString;
    }

  
}
