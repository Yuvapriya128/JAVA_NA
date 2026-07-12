package org.example.springdatajpademo.Ecommerce.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilCustomClaimsTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-signing-secret-1234567890");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);
    }

    @Test
    void generateToken_includesRequiredClaimsForFrontendRoleGuard() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "yuva@gmail.com");
        claims.put("name", "Yuvapriya");
        claims.put("customerId", 1);
        claims.put("role", "ADMIN");

        String token = jwtUtil.generateToken("yuva@gmail.com", claims);
        String[] parts = token.split("\\.");

        assertEquals(3, parts.length, "JWT must have header.payload.signature");

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = OBJECT_MAPPER.readValue(payloadJson, new TypeReference<>() { });

        assertEquals("yuva@gmail.com", payload.get("sub"));
        assertEquals("yuva@gmail.com", payload.get("email"));
        assertEquals("Yuvapriya", payload.get("name"));
        assertEquals(1, ((Number) payload.get("customerId")).intValue());
        assertEquals("ADMIN", payload.get("role"));
        assertTrue(payload.containsKey("iat"));
        assertTrue(payload.containsKey("exp"));
        assertTrue(((Number) payload.get("exp")).longValue() > ((Number) payload.get("iat")).longValue());
    }
}

