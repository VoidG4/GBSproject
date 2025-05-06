package com.gbs.gbsproject.util;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class PasswordUtil {
    // Constants for salt and hash parameters
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;

    // Method to generate a random salt
    public static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    // Method to hash the password using salt
    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // Method to create password hash and salt
    public static String[] createPasswordHashAndSalt(String password) throws Exception {
        byte[] salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return new String[]{hashedPassword, Base64.getEncoder().encodeToString(salt)};
    }

    // Method to verify a password against a hash
    public static boolean verifyPassword(String password, String storedHash, String storedSalt) throws Exception {
        byte[] salt = Base64.getDecoder().decode(storedSalt); // Decode the stored salt
        String hashedPassword = hashPassword(password, salt); // Hash the input password with the salt

        System.out.println(hashedPassword);
        System.out.println(storedSalt);

        return hashedPassword.equals(storedHash); // Compare the hashed password with the stored hash
    }
}