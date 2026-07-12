package org.example.springdatajpademo.Ecommerce.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${app.jwt.secret:change-this-secret-key-for-production-please}")
    private String secret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String generateToken(String email) {
        return generateToken(email, Map.of());
    }

    public String generateToken(String subject, Map<String, Object> additionalClaims) {
        try {
            long issuedAt = Instant.now().getEpochSecond();
            long expiresAt = issuedAt + (expirationMs / 1000);

            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            if (additionalClaims != null) {
                payload.putAll(additionalClaims);
            }
            payload.put("sub", subject);
            payload.put("iat", issuedAt);
            payload.put("exp", expiresAt);

            String encodedHeader = encode(OBJECT_MAPPER.writeValueAsString(header));
            String encodedPayload = encode(OBJECT_MAPPER.writeValueAsString(payload));
            String signature = sign(encodedHeader + "." + encodedPayload);

            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate JWT token", ex);
        }
    }

    public String extractUsername(String token) {
        return (String) extractClaims(token).get("sub");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && isSignatureValid(token);
    }

    private boolean isTokenExpired(String token) {
        Object expObj = extractClaims(token).get("exp");
        if (!(expObj instanceof Number number)) {
            return true;
        }
        long exp = number.longValue();
        long now = Instant.now().getEpochSecond();
        return exp < now;
    }

    private boolean isSignatureValid(String token) {
        try {
            String[] parts = split(token);
            String expected = sign(parts[0] + "." + parts[1]);
            return expected.equals(parts[2]);
        } catch (Exception ex) {
            return false;
        }
    }

    private Map<String, Object> extractClaims(String token) {
        try {
            String[] parts = split(token);
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(payloadJson, new TypeReference<>() { });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JWT token", ex);
        }
    }

    private String[] split(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Token must have exactly 3 parts");
        }
        return parts;
    }

    private String encode(String input) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }
}

