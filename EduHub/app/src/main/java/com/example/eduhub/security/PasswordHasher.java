package com.example.eduhub.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {

    private static final int ROUNDS = 12;

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(ROUNDS));
    }

    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        if (!isBcryptHash(storedHash)) {
            return plainPassword.equals(storedHash);
        }
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public static boolean isBcryptHash(String value) {
        return value != null
                && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }

    public static String hashIfNeeded(String passwordOrHash) {
        if (passwordOrHash == null || passwordOrHash.isEmpty()) {
            return passwordOrHash;
        }
        if (isBcryptHash(passwordOrHash)) {
            return passwordOrHash;
        }
        return hash(passwordOrHash);
    }
}
