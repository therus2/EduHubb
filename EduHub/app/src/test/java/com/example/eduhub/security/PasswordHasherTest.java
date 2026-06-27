package com.example.eduhub.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordHasherTest {

    @Test
    public void hash_returnsNonEmptyString() {
        String hash = PasswordHasher.hash("password123");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    public void hash_startsWithBcryptPrefix() {
        String hash = PasswordHasher.hash("password123");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
    }

    @Test
    public void hash_differentPasswordsProduceDifferentHashes() {
        String hash1 = PasswordHasher.hash("password1");
        String hash2 = PasswordHasher.hash("password2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void verify_returnsTrueForCorrectPassword() {
        String hash = PasswordHasher.hash("correctPassword");
        assertTrue(PasswordHasher.verify("correctPassword", hash));
    }

    @Test
    public void verify_returnsFalseForIncorrectPassword() {
        String hash = PasswordHasher.hash("correctPassword");
        assertFalse(PasswordHasher.verify("wrongPassword", hash));
    }

    @Test
    public void verify_returnsFalseForNullPlainPassword() {
        assertFalse(PasswordHasher.verify(null, "$2a$12$somehash"));
    }

    @Test
    public void verify_returnsFalseForNullStoredHash() {
        assertFalse(PasswordHasher.verify("password", null));
    }

    @Test
    public void verify_returnsFalseForEmptyStoredHash() {
        assertFalse(PasswordHasher.verify("password", ""));
    }

    @Test
    public void verify_fallsBackToPlainComparisonForNonBcrypt() {
        assertTrue(PasswordHasher.verify("plain", "plain"));
    }

    @Test
    public void verify_returnsFalseForNonBcryptMismatch() {
        assertFalse(PasswordHasher.verify("plain1", "plain2"));
    }

    @Test
    public void isBcryptHash_returnsTrueForValidBcrypt() {
        String hash = PasswordHasher.hash("test");
        assertTrue(PasswordHasher.isBcryptHash(hash));
    }

    @Test
    public void isBcryptHash_returnsFalseForPlainText() {
        assertFalse(PasswordHasher.isBcryptHash("plainPassword"));
    }

    @Test
    public void isBcryptHash_returnsFalseForNull() {
        assertFalse(PasswordHasher.isBcryptHash(null));
    }

    @Test
    public void isBcryptHash_returnsTrueFor2bPrefix() {
        assertTrue(PasswordHasher.isBcryptHash("$2b$10$abcdef"));
    }

    @Test
    public void isBcryptHash_returnsTrueFor2yPrefix() {
        assertTrue(PasswordHasher.isBcryptHash("$2y$10$abcdef"));
    }

    @Test
    public void hashIfNeeded_returnsHashForPlainText() {
        String result = PasswordHasher.hashIfNeeded("newPassword");
        assertTrue(PasswordHasher.isBcryptHash(result));
    }

    @Test
    public void hashIfNeeded_returnsSameHashForBcrypt() {
        String hash = PasswordHasher.hash("existingPassword");
        String result = PasswordHasher.hashIfNeeded(hash);
        assertEquals(hash, result);
    }

    @Test
    public void hashIfNeeded_returnsNullForNullInput() {
        assertNull(PasswordHasher.hashIfNeeded(null));
    }

    @Test
    public void hashIfNeeded_returnsEmptyForEmptyInput() {
        assertEquals("", PasswordHasher.hashIfNeeded(""));
    }

    @Test
    public void hash_usesHighRounds() {
        long start = System.currentTimeMillis();
        PasswordHasher.hash("benchmark");
        long duration = System.currentTimeMillis() - start;
        assertTrue("Hash took " + duration + "ms, expected > 50ms", duration > 50);
    }
}
