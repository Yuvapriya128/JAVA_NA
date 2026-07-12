package org.example.springdatajpademo.Ecommerce.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordDebugTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void hashFromDatabaseDoesNotMatchYuva123() {
        String dbHash = "$2a$10$N9qo8uLOickgx2ZMRZoMYOjlH6K9gfzHrGnKvYFT7n3SpP7PhzHDe";
        assertFalse(encoder.matches("yuva123", dbHash));
    }

    @Test
    void generatedHashMatchesYuva123() {
        String generatedHash = "$2a$10$UVr44648./rA8JZD3u4M3.63wyU3AUOzGL.Z8FvTvlIM5bPKYuEkS";
        assertTrue(encoder.matches("yuva123", generatedHash));
    }
}

