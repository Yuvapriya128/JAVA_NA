package org.example.springdatajpademo.Ecommerce.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "unit-test-secret-key-1234567890");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 60000L);
    }

    @Test
    void generateAndValidateToken_success() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();

        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("user@example.com", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_failsForDifferentUser() {
        UserDetails tokenOwner = User.withUsername("owner@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();

        UserDetails anotherUser = User.withUsername("another@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();

        String token = jwtUtil.generateToken(tokenOwner);

        assertFalse(jwtUtil.isTokenValid(token, anotherUser));
    }
}

