package com.gbs.gbsproject.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testGenerateSaltLengthAndRandomness() {
        byte[] salt1 = PasswordUtil.generateSalt();
        byte[] salt2 = PasswordUtil.generateSalt();

        assertEquals(16, salt1.length, "Salt length should be 16 bytes");
        assertEquals(16, salt2.length);
        assertFalse(java.util.Arrays.equals(salt1, salt2), "Salts should be random");
    }

    @Test
    void testHashPasswordConsistencyWithSameSalt() throws Exception {
        String password = "mySecret123";
        byte[] salt = PasswordUtil.generateSalt();

        String hash1 = PasswordUtil.hashPassword(password, salt);
        String hash2 = PasswordUtil.hashPassword(password, salt);

        assertEquals(hash1, hash2, "Hashes should be consistent for same input");
    }

    @Test
    void testCreatePasswordHashAndSaltStructure() throws Exception {
        String password = "examplePassword";
        String[] result = PasswordUtil.createPasswordHashAndSalt(password);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertFalse(result[0].isEmpty(), "Hash should not be empty");
        assertFalse(result[1].isEmpty(), "Salt should not be empty");
    }

    @Test
    void testVerifyPasswordSuccess() throws Exception {
        String password = "correctPassword";
        String[] hashAndSalt = PasswordUtil.createPasswordHashAndSalt(password);

        boolean match = PasswordUtil.verifyPassword(password, hashAndSalt[0], hashAndSalt[1]);

        assertTrue(match, "Password should verify correctly");
    }

    @Test
    void testVerifyPasswordFailure() throws Exception {
        String correctPassword = "password123";
        String wrongPassword = "wrongPassword";
        String[] hashAndSalt = PasswordUtil.createPasswordHashAndSalt(correctPassword);

        boolean match = PasswordUtil.verifyPassword(wrongPassword, hashAndSalt[0], hashAndSalt[1]);

        assertFalse(match, "Password verification should fail for incorrect password");
    }
}
